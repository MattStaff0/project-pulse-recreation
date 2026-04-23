package team.projectpulse.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import team.projectpulse.activity.Activity;
import team.projectpulse.student.Student;
import team.projectpulse.student.StudentRepository;
import team.projectpulse.user.PeerEvaluationUser;
import team.projectpulse.user.UserRepository;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component("authz")
public class AuthorizationService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public AuthorizationService(UserRepository userRepository, StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    public boolean isSelf(Long userId) {
        return userId != null && userId.equals(currentUser().getId());
    }

    public PeerEvaluationUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("No authenticated user");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("Authenticated user not found"));
    }

    public boolean hasRole(String role) {
        return currentRoles().contains(role);
    }

    public void requireSelfOrAdmin(Long userId) {
        if (!hasRole("admin") && !isSelf(userId)) {
            throw new AccessDeniedException("No permission");
        }
    }

    public void requireCanAccessStudent(Long studentId) {
        if (hasRole("admin") || hasRole("instructor") || isSelf(studentId)) {
            return;
        }
        throw new AccessDeniedException("No permission");
    }

    public void requireCanReadSection(Long sectionId) {
        if (hasRole("admin") || hasRole("instructor")) {
            return;
        }
        Student currentStudent = currentStudent();
        if (sectionId != null && currentStudent.getSection() != null
                && sectionId.equals(currentStudent.getSection().getId())) {
            return;
        }
        throw new AccessDeniedException("No permission");
    }

    public void requireCanReadTeam(Long teamId) {
        if (hasRole("admin") || hasRole("instructor")) {
            return;
        }
        Student currentStudent = currentStudent();
        if (teamId != null && currentStudent.getTeam() != null
                && teamId.equals(currentStudent.getTeam().getId())) {
            return;
        }
        throw new AccessDeniedException("No permission");
    }

    public void requireCanReadActivities(Long studentId, Long teamId) {
        if (hasRole("admin") || hasRole("instructor")) {
            return;
        }

        if (!hasRole("student")) {
            throw new AccessDeniedException("No permission");
        }

        Student currentStudent = currentStudent();
        if (studentId != null && studentId.equals(currentStudent.getId())) {
            return;
        }
        if (teamId != null && currentStudent.getTeam() != null && teamId.equals(currentStudent.getTeam().getId())) {
            return;
        }

        throw new AccessDeniedException("No permission");
    }

    public void requireCanReadActivity(Activity activity) {
        if (hasRole("admin") || hasRole("instructor")) {
            return;
        }
        if (activity.getStudent() != null && isSelf(activity.getStudent().getId())) {
            return;
        }
        throw new AccessDeniedException("No permission");
    }

    public void requireCanCreateActivityForStudent(Long studentId) {
        if (hasRole("admin")) {
            return;
        }
        if (hasRole("student") && isSelf(studentId)) {
            return;
        }
        throw new AccessDeniedException("No permission");
    }

    public void requireCanManageActivity(Activity activity) {
        if (hasRole("admin")) {
            return;
        }
        if (activity.getStudent() != null && hasRole("student") && isSelf(activity.getStudent().getId())) {
            return;
        }
        throw new AccessDeniedException("No permission");
    }

    public void requireCanReadEvaluations(Long evaluatorId, Long evaluateeId) {
        if (hasRole("admin") || hasRole("instructor")) {
            return;
        }
        Long currentUserId = currentUser().getId();
        if ((evaluatorId != null && evaluatorId.equals(currentUserId))
                || (evaluateeId != null && evaluateeId.equals(currentUserId))) {
            return;
        }
        throw new AccessDeniedException("No permission");
    }

    public void requireStudentEvaluationSubmission(Long evaluatorId, Long evaluateeId) {
        if (!hasRole("student") || !isSelf(evaluatorId)) {
            throw new AccessDeniedException("No permission");
        }

        Student evaluator = studentRepository.findById(evaluatorId)
                .orElseThrow(() -> new AccessDeniedException("Evaluator not found"));
        Student evaluatee = studentRepository.findById(evaluateeId)
                .orElseThrow(() -> new AccessDeniedException("Evaluatee not found"));

        if (evaluator.getTeam() == null || evaluatee.getTeam() == null
                || !evaluator.getTeam().getId().equals(evaluatee.getTeam().getId())) {
            throw new AccessDeniedException("Students can only evaluate teammates");
        }
    }

    private Student currentStudent() {
        PeerEvaluationUser user = currentUser();
        if (!(user instanceof Student student)) {
            throw new AccessDeniedException("Current user is not a student");
        }
        return student;
    }

    private Set<String> currentRoles() {
        PeerEvaluationUser user = currentUser();
        return Arrays.stream(user.getRoles().split(" "))
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .collect(Collectors.toSet());
    }
}
