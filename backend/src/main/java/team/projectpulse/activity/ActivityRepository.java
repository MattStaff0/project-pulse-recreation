package team.projectpulse.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByStudentIdAndWeek(Long studentId, Integer week);
    List<Activity> findByTeamIdAndWeek(Long teamId, Integer week);
    List<Activity> findByStudentId(Long studentId);
    List<Activity> findByTeamId(Long teamId);
    void deleteByStudentId(Long studentId);
    void deleteByTeamId(Long teamId);
}
