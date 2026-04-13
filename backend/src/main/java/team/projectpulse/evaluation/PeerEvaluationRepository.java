package team.projectpulse.evaluation;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PeerEvaluationRepository extends JpaRepository<PeerEvaluation, Long> {
    List<PeerEvaluation> findByEvaluatorIdAndWeek(Long evaluatorId, Integer week);
    List<PeerEvaluation> findByEvaluateeIdAndWeek(Long evaluateeId, Integer week);
    List<PeerEvaluation> findByEvaluateeId(Long evaluateeId);
    Optional<PeerEvaluation> findByEvaluatorIdAndEvaluateeIdAndWeek(Long evaluatorId, Long evaluateeId, Integer week);
    List<PeerEvaluation> findByWeek(Integer week);
}
