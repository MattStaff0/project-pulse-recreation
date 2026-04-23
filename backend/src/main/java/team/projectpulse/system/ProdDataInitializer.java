package team.projectpulse.system;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import team.projectpulse.instructor.Instructor;
import team.projectpulse.instructor.InstructorRepository;

@Component
@Profile("prod")
public class ProdDataInitializer implements CommandLineRunner {

    private final InstructorRepository instructorRepository;
    private final PasswordEncoder passwordEncoder;

    public ProdDataInitializer(InstructorRepository instructorRepository, PasswordEncoder passwordEncoder) {
        this.instructorRepository = instructorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (instructorRepository.count() == 0) {
            Instructor admin = new Instructor();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@projectpulse.com");
            admin.setPassword(passwordEncoder.encode("Admin123"));
            admin.setRoles("admin");
            admin.setEnabled(true);
            instructorRepository.save(admin);
        }
    }
}
