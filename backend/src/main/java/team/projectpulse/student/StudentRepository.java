package team.projectpulse.student;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    List<Student> findBySectionId(Long sectionId);
    List<Student> findByTeamId(Long teamId);
    List<Student> findBySectionIdAndTeamIsNull(Long sectionId);
}
