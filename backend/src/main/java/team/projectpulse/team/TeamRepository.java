package team.projectpulse.team;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findBySectionId(Long sectionId);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
}
