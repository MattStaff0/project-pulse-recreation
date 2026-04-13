package team.projectpulse.evaluation;

import org.springframework.web.bind.annotation.*;
import team.projectpulse.rubric.Criterion;
import team.projectpulse.rubric.CriterionRepository;
import team.projectpulse.student.Student;
import team.projectpulse.student.StudentService;
import team.projectpulse.system.Result;
import team.projectpulse.system.StatusCode;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/evaluations")
public class PeerEvaluationController {

    private final PeerEvaluationService evaluationService;
    private final StudentService studentService;
    private final CriterionRepository criterionRepository;

    public PeerEvaluationController(PeerEvaluationService evaluationService, StudentService studentService, CriterionRepository criterionRepository) {
        this.evaluationService = evaluationService;
        this.studentService = studentService;
        this.criterionRepository = criterionRepository;
    }

    @GetMapping
    public Result findByEvaluator(@RequestParam(required = false) Long evaluatorId,
                                   @RequestParam(required = false) Long evaluateeId,
                                   @RequestParam(required = false) Integer week) {
        List<PeerEvaluation> evals;
        if (evaluatorId != null && week != null) {
            evals = evaluationService.findByEvaluatorAndWeek(evaluatorId, week);
        } else if (evaluateeId != null && week != null) {
            evals = evaluationService.findByEvaluateeAndWeek(evaluateeId, week);
        } else if (evaluateeId != null) {
            evals = evaluationService.findByEvaluatee(evaluateeId);
        } else if (week != null) {
            evals = evaluationService.findByWeek(week);
        } else {
            evals = List.of();
        }
        return new Result(true, StatusCode.SUCCESS, "Find evaluations successfully",
                evals.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id) {
        return new Result(true, StatusCode.SUCCESS, "Find evaluation successfully", toDto(evaluationService.findById(id)));
    }

    @PostMapping
    @SuppressWarnings("unchecked")
    public Result create(@RequestBody Map<String, Object> body) {
        PeerEvaluation eval = new PeerEvaluation();
        eval.setEvaluator(studentService.findById(Long.valueOf(body.get("evaluatorId").toString())));
        eval.setEvaluatee(studentService.findById(Long.valueOf(body.get("evaluateeId").toString())));
        eval.setWeek(Integer.valueOf(body.get("week").toString()));
        eval.setPublicComment((String) body.get("publicComment"));
        eval.setPrivateComment((String) body.get("privateComment"));

        List<Map<String, Object>> ratingsData = (List<Map<String, Object>>) body.get("ratings");
        if (ratingsData != null) {
            for (Map<String, Object> rd : ratingsData) {
                Rating rating = new Rating();
                rating.setCriterion(criterionRepository.findById(Long.valueOf(rd.get("criterionId").toString())).orElse(null));
                rating.setScore(Integer.valueOf(rd.get("score").toString()));
                eval.addRating(rating);
            }
        }
        return new Result(true, StatusCode.SUCCESS, "Evaluation submitted successfully", toDto(evaluationService.save(eval)));
    }

    @PutMapping("/{id}")
    @SuppressWarnings("unchecked")
    public Result update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        PeerEvaluation update = new PeerEvaluation();
        update.setPublicComment((String) body.get("publicComment"));
        update.setPrivateComment((String) body.get("privateComment"));

        List<Map<String, Object>> ratingsData = (List<Map<String, Object>>) body.get("ratings");
        if (ratingsData != null) {
            for (Map<String, Object> rd : ratingsData) {
                Rating rating = new Rating();
                rating.setCriterion(criterionRepository.findById(Long.valueOf(rd.get("criterionId").toString())).orElse(null));
                rating.setScore(Integer.valueOf(rd.get("score").toString()));
                update.addRating(rating);
            }
        }
        return new Result(true, StatusCode.SUCCESS, "Evaluation updated successfully", toDto(evaluationService.update(id, update)));
    }

    // Student views own peer evaluation report
    @GetMapping("/report/student/{studentId}")
    public Result studentReport(@PathVariable Long studentId, @RequestParam Integer week) {
        return new Result(true, StatusCode.SUCCESS, "Report generated", evaluationService.generateStudentReport(studentId, week));
    }

    // Instructor generates section-wide report
    @GetMapping("/report/section/{sectionId}")
    public Result sectionReport(@PathVariable Long sectionId, @RequestParam Integer week) {
        return new Result(true, StatusCode.SUCCESS, "Section report generated", evaluationService.generateSectionReport(sectionId, week));
    }

    private Map<String, Object> toDto(PeerEvaluation e) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", e.getId());
        dto.put("evaluatorId", e.getEvaluator().getId());
        dto.put("evaluatorName", e.getEvaluator().getFirstName() + " " + e.getEvaluator().getLastName());
        dto.put("evaluateeId", e.getEvaluatee().getId());
        dto.put("evaluateeName", e.getEvaluatee().getFirstName() + " " + e.getEvaluatee().getLastName());
        dto.put("week", e.getWeek());
        dto.put("totalScore", e.getTotalScore());
        dto.put("publicComment", e.getPublicComment());
        dto.put("privateComment", e.getPrivateComment());
        dto.put("ratings", e.getRatings().stream().map(r -> {
            Map<String, Object> rDto = new LinkedHashMap<>();
            rDto.put("id", r.getId());
            rDto.put("criterionId", r.getCriterion() != null ? r.getCriterion().getId() : null);
            rDto.put("criterionName", r.getCriterion() != null ? r.getCriterion().getName() : null);
            rDto.put("score", r.getScore());
            return rDto;
        }).collect(Collectors.toList()));
        dto.put("createdAt", e.getCreatedAt());
        dto.put("updatedAt", e.getUpdatedAt());
        return dto;
    }
}
