package team.projectpulse.rubric;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import team.projectpulse.course.Course;
import team.projectpulse.course.CourseRepository;
import team.projectpulse.security.AuthorizationService;
import team.projectpulse.system.Result;
import team.projectpulse.system.StatusCode;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/rubrics")
public class RubricController {

    private final RubricService rubricService;
    private final CourseRepository courseRepository;
    private final AuthorizationService authorizationService;

    public RubricController(RubricService rubricService, CourseRepository courseRepository,
                            AuthorizationService authorizationService) {
        this.rubricService = rubricService;
        this.courseRepository = courseRepository;
        this.authorizationService = authorizationService;
    }

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public Result findAll(@RequestParam(required = false) Long courseId) {
        List<Rubric> rubrics = courseId != null ? rubricService.findByCourseId(courseId) : rubricService.findAll();
        return new Result(true, StatusCode.SUCCESS, "Find rubrics successfully",
                rubrics.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id) {
        authorizationService.requireCanReadRubric(id);
        return new Result(true, StatusCode.SUCCESS, "Find rubric successfully", toDto(rubricService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    @SuppressWarnings("unchecked")
    public Result create(@RequestBody Map<String, Object> body) {
        Rubric rubric = new Rubric();
        rubric.setName((String) body.get("name"));
        if (body.get("courseId") != null) {
            Course course = courseRepository.findById(Long.valueOf(body.get("courseId").toString())).orElse(null);
            rubric.setCourse(course);
        }
        List<Map<String, Object>> criteriaData = (List<Map<String, Object>>) body.get("criteria");
        if (criteriaData != null) {
            for (Map<String, Object> cd : criteriaData) {
                Criterion c = new Criterion();
                c.setName((String) cd.get("name"));
                c.setDescription((String) cd.get("description"));
                c.setMaxScore(Double.valueOf(cd.get("maxScore").toString()));
                rubric.addCriterion(c);
            }
        }
        return new Result(true, StatusCode.SUCCESS, "Rubric created successfully", toDto(rubricService.save(rubric)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    @SuppressWarnings("unchecked")
    public Result update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Rubric update = new Rubric();
        update.setName((String) body.get("name"));
        List<Map<String, Object>> criteriaData = (List<Map<String, Object>>) body.get("criteria");
        if (criteriaData != null) {
            for (Map<String, Object> cd : criteriaData) {
                Criterion c = new Criterion();
                c.setName((String) cd.get("name"));
                c.setDescription((String) cd.get("description"));
                c.setMaxScore(Double.valueOf(cd.get("maxScore").toString()));
                update.addCriterion(c);
            }
        }
        return new Result(true, StatusCode.SUCCESS, "Rubric updated successfully", toDto(rubricService.update(id, update)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public Result delete(@PathVariable Long id) {
        rubricService.delete(id);
        return new Result(true, StatusCode.SUCCESS, "Rubric deleted successfully");
    }

    private Map<String, Object> toDto(Rubric r) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", r.getId());
        dto.put("name", r.getName());
        dto.put("courseId", r.getCourse() != null ? r.getCourse().getId() : null);
        dto.put("criteria", r.getCriteria().stream().map(c -> {
            Map<String, Object> cDto = new LinkedHashMap<>();
            cDto.put("id", c.getId());
            cDto.put("name", c.getName());
            cDto.put("description", c.getDescription());
            cDto.put("maxScore", c.getMaxScore());
            return cDto;
        }).collect(Collectors.toList()));
        return dto;
    }
}
