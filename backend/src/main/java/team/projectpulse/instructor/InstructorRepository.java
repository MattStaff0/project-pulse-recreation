package team.projectpulse.instructor;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    Optional<Instructor> findByEmail(String email);
    List<Instructor> findByEnabledTrue();
    List<Instructor> findByEnabledFalse();
}
