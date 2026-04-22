package team.projectpulse.evaluation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.security.AuthorizationService;
import team.projectpulse.student.Student;
import team.projectpulse.student.StudentRepository;
import team.projectpulse.system.exception.ObjectNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PeerEvaluationService {

    private final PeerEvaluationRepository peerEvaluationRepository;
    private final StudentRepository studentRepository;
    private final AuthorizationService authorizationService;

    public PeerEvaluationService(PeerEvaluationRepository peerEvaluationRepository,
                                  StudentRepository studentRepository,
                                  AuthorizationService authorizationService) {
        this.peerEvaluationRepository = peerEvaluationRepository;
        this.studentRepository = studentRepository;
        this.authorizationService = authorizationService;
    }

    public PeerEvaluation findById(Long id) {
        return peerEvaluationRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("peer evaluation", id));
    }

    public List<PeerEvaluation> findByEvaluatorAndWeek(Long evaluatorId, Integer week) {
        return peerEvaluationRepository.findByEvaluatorIdAndWeek(evaluatorId, week);
    }

    public List<PeerEvaluation> findByEvaluateeAndWeek(Long evaluateeId, Integer week) {
        return peerEvaluationRepository.findByEvaluateeIdAndWeek(evaluateeId, week);
    }

    public List<PeerEvaluation> findByEvaluatee(Long evaluateeId) {
        return peerEvaluationRepository.findByEvaluateeId(evaluateeId);
    }

    public List<PeerEvaluation> findByWeek(Integer week) {
        return peerEvaluationRepository.findByWeek(week);
    }

    public PeerEvaluation save(PeerEvaluation evaluation) {
        if (evaluation.getEvaluator() == null || evaluation.getEvaluatee() == null) {
            throw new IllegalArgumentException("Evaluator and evaluatee are required");
        }
        authorizationService.requireStudentEvaluationSubmission(
                evaluation.getEvaluator().getId(),
                evaluation.getEvaluatee().getId()
        );
        if (peerEvaluationRepository.findByEvaluatorIdAndEvaluateeIdAndWeek(
                evaluation.getEvaluator().getId(),
                evaluation.getEvaluatee().getId(),
                evaluation.getWeek()
        ).isPresent()) {
            throw new IllegalArgumentException("Peer evaluation already submitted for this teammate and week");
        }
        validateActiveWeek(evaluation.getEvaluator(), evaluation.getWeek());
        // Calculate total score from ratings
        double total = evaluation.getRatings().stream()
                .mapToDouble(r -> r.getScore() != null ? r.getScore() : 0)
                .sum();
        evaluation.setTotalScore(total);
        return peerEvaluationRepository.save(evaluation);
    }

    public PeerEvaluation update(Long id, PeerEvaluation update) {
        throw new IllegalArgumentException("Peer evaluations cannot be edited once submitted");
    }

    // Generate report for a student for a specific week
    public Map<String, Object> generateStudentReport(Long studentId, Integer week) {
        authorizationService.requireCanAccessStudent(studentId);
        List<PeerEvaluation> evals = peerEvaluationRepository.findByEvaluateeIdAndWeek(studentId, week);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ObjectNotFoundException("student", studentId));

        Map<String, Object> report = new HashMap<>();
        report.put("studentName", student.getFirstName() + " " + student.getLastName());
        report.put("week", week);

        if (evals.isEmpty()) {
            report.put("averageScores", Collections.emptyMap());
            report.put("averageTotal", 0.0);
            report.put("publicComments", Collections.emptyList());
            return report;
        }

        // Calculate average scores per criterion
        Map<String, List<Integer>> criterionScores = new LinkedHashMap<>();
        for (PeerEvaluation eval : evals) {
            for (Rating rating : eval.getRatings()) {
                String criterionName = rating.getCriterion().getName();
                criterionScores.computeIfAbsent(criterionName, k -> new ArrayList<>()).add(rating.getScore());
            }
        }

        Map<String, Double> averageScores = new LinkedHashMap<>();
        for (Map.Entry<String, List<Integer>> entry : criterionScores.entrySet()) {
            averageScores.put(entry.getKey(), entry.getValue().stream().mapToInt(Integer::intValue).average().orElse(0));
        }

        double averageTotal = evals.stream().mapToDouble(PeerEvaluation::getTotalScore).average().orElse(0);

        List<String> publicComments = evals.stream()
                .map(PeerEvaluation::getPublicComment)
                .filter(c -> c != null && !c.isBlank())
                .collect(Collectors.toList());

        report.put("averageScores", averageScores);
        report.put("averageTotal", averageTotal);
        report.put("publicComments", publicComments);

        return report;
    }

    // Generate section-wide report for a specific week
    public List<Map<String, Object>> generateSectionReport(Long sectionId, Integer week) {
        List<Student> students = studentRepository.findBySectionId(sectionId);
        List<Map<String, Object>> reports = new ArrayList<>();
        for (Student student : students) {
            reports.add(generateStudentReport(student.getId(), week));
        }
        return reports;
    }

    private void validateActiveWeek(Student evaluator, Integer week) {
        if (week == null || week < 1) {
            throw new IllegalArgumentException("Week must be a positive integer");
        }
        if (evaluator.getSection() == null || evaluator.getSection().getActiveWeeks() == null) {
            return;
        }
        Set<Integer> activeWeeks = Arrays.stream(
                        evaluator.getSection().getActiveWeeks().replace("[", "").replace("]", "").split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::valueOf)
                .collect(Collectors.toSet());
        if (!activeWeeks.isEmpty() && !activeWeeks.contains(week)) {
            throw new IllegalArgumentException("Peer evaluations can only be submitted during active weeks");
        }
    }
}
