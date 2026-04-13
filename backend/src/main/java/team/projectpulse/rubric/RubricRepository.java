package team.projectpulse.rubric;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RubricRepository extends JpaRepository<Rubric, Long> {
    List<Rubric> findByCourseId(Long courseId);
    boolean existsByName(String name);
}
