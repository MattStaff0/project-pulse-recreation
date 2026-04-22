package team.projectpulse.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.instructor.Instructor;
import team.projectpulse.instructor.InstructorRepository;
import team.projectpulse.security.AuthorizationService;
import team.projectpulse.section.Section;
import team.projectpulse.section.SectionRepository;
import team.projectpulse.student.Student;
import team.projectpulse.student.StudentRepository;
import team.projectpulse.system.exception.ObjectNotFoundException;

import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final InvitationRepository invitationRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final SectionRepository sectionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final AuthorizationService authorizationService;

    private final String frontEndUrl;

    public UserService(UserRepository userRepository,
                       StudentRepository studentRepository,
                       InstructorRepository instructorRepository,
                       InvitationRepository invitationRepository,
                       PasswordResetTokenRepository resetTokenRepository,
                       SectionRepository sectionRepository,
                       PasswordEncoder passwordEncoder,
                       JavaMailSender mailSender,
                       AuthorizationService authorizationService,
                       @Value("${front-end.url:http://localhost:5173}") String frontEndUrl) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.instructorRepository = instructorRepository;
        this.invitationRepository = invitationRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.sectionRepository = sectionRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.authorizationService = authorizationService;
        this.frontEndUrl = frontEndUrl;
    }

    public PeerEvaluationUser findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("user", id));
    }

    public PeerEvaluationUser findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ObjectNotFoundException("User not found with email: " + email));
    }

    public PeerEvaluationUser updateProfile(Long id, String firstName, String lastName, String email) {
        authorizationService.requireSelfOrAdmin(id);
        PeerEvaluationUser user = findById(id);
        userRepository.findByEmail(email)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("An account with this email already exists");
                });
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        return userRepository.save(user);
    }

    public void changePassword(Long id, String oldPassword, String newPassword) {
        authorizationService.requireSelfOrAdmin(id);
        PeerEvaluationUser user = findById(id);
        if (!authorizationService.hasRole("admin") && !passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Invitation flow
    public void sendInvitations(String[] emails, String role, Long courseId, Long sectionId, String senderName) {
        for (String email : emails) {
            String trimmedEmail = email.trim();
            if (trimmedEmail.isEmpty()) continue;

            if (userRepository.existsByEmail(trimmedEmail)) continue;

            String token = UUID.randomUUID().toString();
            UserInvitation invitation = new UserInvitation();
            invitation.setEmail(trimmedEmail);
            invitation.setToken(token);
            invitation.setRole(role);
            invitation.setCourseId(courseId);
            invitation.setSectionId(sectionId);
            invitationRepository.save(invitation);

            String registrationLink = frontEndUrl + "/register?email=" + trimmedEmail
                    + "&token=" + token + "&courseId=" + courseId
                    + "&sectionId=" + sectionId + "&role=" + role;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(trimmedEmail);
            message.setSubject("Welcome to Project Pulse - Complete Your Registration");
            message.setText("Hello,\n\n" + senderName + " has invited you to join Project Pulse. "
                    + "To complete your registration, please use the link below:\n\n"
                    + registrationLink + "\n\n"
                    + "Best regards,\nProject Pulse Team");
            try {
                mailSender.send(message);
            } catch (Exception e) {
                // Log but don't fail the whole batch
            }
        }
    }

    public UserInvitation validateInvitation(String token) {
        return invitationRepository.findByToken(token)
                .orElseThrow(() -> new ObjectNotFoundException("Invalid invitation token"));
    }

    // Registration
    public PeerEvaluationUser register(String email, String firstName, String lastName,
                                        String password, String token) {
        UserInvitation invitation = validateInvitation(token);
        if (invitation.isAccepted()) {
            throw new IllegalArgumentException("This invitation has already been used");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        PeerEvaluationUser user;
        if ("instructor".equals(invitation.getRole())) {
            Instructor instructor = new Instructor();
            instructor.setRoles("instructor");
            user = instructor;
        } else {
            Student student = new Student();
            student.setRoles("student");
            if (invitation.getSectionId() != null) {
                Section section = sectionRepository.findById(invitation.getSectionId()).orElse(null);
                student.setSection(section);
            }
            user = student;
        }

        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);

        invitation.setAccepted(true);
        invitationRepository.save(invitation);

        return userRepository.save(user);
    }

    // Password reset
    public void requestPasswordReset(String email) {
        if (!userRepository.existsByEmail(email)) {
            return; // Don't reveal if email exists
        }
        resetTokenRepository.deleteByEmail(email);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setToken(token);
        resetTokenRepository.save(resetToken);

        String resetLink = frontEndUrl + "/reset-password?email=" + email + "&token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Project Pulse - Password Reset");
        message.setText("Hello,\n\nYou requested a password reset. Use the link below:\n\n"
                + resetLink + "\n\nThis link expires in 1 hour.\n\nBest regards,\nProject Pulse Team");
        try {
            mailSender.send(message);
        } catch (Exception e) {
            // log
        }
    }

    public void resetPassword(String email, String token, String newPassword) {
        PasswordResetToken resetToken = resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new ObjectNotFoundException("Invalid reset token"));

        if (!resetToken.getEmail().equals(email)) {
            throw new IllegalArgumentException("Token does not match email");
        }

        if (resetToken.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token has expired");
        }

        PeerEvaluationUser user = findByEmail(email);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetTokenRepository.deleteByEmail(email);
    }
}
