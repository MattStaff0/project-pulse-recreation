package team.projectpulse.section;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByCourseId(Long courseId);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
}
