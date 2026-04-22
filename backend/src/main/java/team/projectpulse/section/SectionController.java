package team.projectpulse.section;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import team.projectpulse.course.Course;
import team.projectpulse.course.CourseRepository;
import team.projectpulse.rubric.Rubric;
import team.projectpulse.rubric.RubricRepository;
import team.projectpulse.system.Result;
import team.projectpulse.system.StatusCode;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/sections")
@PreAuthorize("hasRole('admin')")
public class SectionController {

    private final SectionService sectionService;
    private final CourseRepository courseRepository;
    private final RubricRepository rubricRepository;

    public SectionController(SectionService sectionService, CourseRepository courseRepository, RubricRepository rubricRepository) {
        this.sectionService = sectionService;
        this.courseRepository = courseRepository;
        this.rubricRepository = rubricRepository;
    }

    @GetMapping
    public Result findAll(@RequestParam(required = false) Long courseId,
                          @RequestParam(required = false) String name) {
        List<Section> sections;
        if (courseId != null) {
            sections = sectionService.findByCourseId(courseId);
        } else {
            sections = sectionService.findAll();
        }
        if (name != null && !name.isBlank()) {
            sections = sections.stream()
                    .filter(s -> s.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new Result(true, StatusCode.SUCCESS, "Find sections successfully", sections.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id) {
        Section section = sectionService.findById(id);
        return new Result(true, StatusCode.SUCCESS, "Find section successfully", toDetailDto(section));
    }

    @PostMapping
    public Result create(@RequestBody Map<String, Object> body) {
        Section section = new Section();
        section.setName((String) body.get("name"));
        if (body.get("startDate") != null) section.setStartDate(LocalDate.parse((String) body.get("startDate")));
        if (body.get("endDate") != null) section.setEndDate(LocalDate.parse((String) body.get("endDate")));
        if (body.get("courseId") != null) {
            Long courseId = Long.valueOf(body.get("courseId").toString());
            Course course = courseRepository.findById(courseId).orElse(null);
            section.setCourse(course);
        }
        if (body.get("rubricId") != null) {
            Long rubricId = Long.valueOf(body.get("rubricId").toString());
            Rubric rubric = rubricRepository.findById(rubricId).orElse(null);
            section.setRubric(rubric);
        }
        Section saved = sectionService.save(section);
        return new Result(true, StatusCode.SUCCESS, "Section created successfully", toDto(saved));
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Section update = new Section();
        update.setName((String) body.get("name"));
        if (body.get("startDate") != null) update.setStartDate(LocalDate.parse((String) body.get("startDate")));
        if (body.get("endDate") != null) update.setEndDate(LocalDate.parse((String) body.get("endDate")));
        if (body.get("rubricId") != null) {
            Long rubricId = Long.valueOf(body.get("rubricId").toString());
            update.setRubric(rubricRepository.findById(rubricId).orElse(null));
        }
        Section saved = sectionService.update(id, update);
        return new Result(true, StatusCode.SUCCESS, "Section updated successfully", toDto(saved));
    }

    @PutMapping("/{id}/active-weeks")
    public Result updateActiveWeeks(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Section section = sectionService.updateActiveWeeks(id, body.get("activeWeeks"));
        return new Result(true, StatusCode.SUCCESS, "Active weeks updated", toDto(section));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        sectionService.delete(id);
        return new Result(true, StatusCode.SUCCESS, "Section deleted successfully");
    }

    private Map<String, Object> toDto(Section s) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", s.getId());
        dto.put("name", s.getName());
        dto.put("startDate", s.getStartDate());
        dto.put("endDate", s.getEndDate());
        dto.put("activeWeeks", s.getActiveWeeks());
        dto.put("courseId", s.getCourse() != null ? s.getCourse().getId() : null);
        dto.put("rubricId", s.getRubric() != null ? s.getRubric().getId() : null);
        dto.put("numberOfTeams", s.getTeams().size());
        dto.put("numberOfStudents", s.getStudents().size());
        return dto;
    }

    private Map<String, Object> toDetailDto(Section s) {
        Map<String, Object> dto = toDto(s);
        dto.put("teams", s.getTeams().stream().map(t -> {
            Map<String, Object> teamDto = new LinkedHashMap<>();
            teamDto.put("id", t.getId());
            teamDto.put("name", t.getName());
            teamDto.put("students", t.getStudents().stream().map(st -> Map.of(
                    "id", st.getId(), "firstName", st.getFirstName(), "lastName", st.getLastName(), "email", st.getEmail()
            )).collect(Collectors.toList()));
            teamDto.put("instructors", t.getInstructors().stream().map(i -> Map.of(
                    "id", i.getId(), "firstName", i.getFirstName(), "lastName", i.getLastName()
            )).collect(Collectors.toList()));
            return teamDto;
        }).collect(Collectors.toList()));
        dto.put("instructors", s.getInstructors().stream().map(i -> Map.of(
                "id", i.getId(), "firstName", i.getFirstName(), "lastName", i.getLastName()
        )).collect(Collectors.toList()));
        dto.put("unassignedStudents", s.getStudents().stream()
                .filter(st -> st.getTeam() == null)
                .map(st -> Map.of("id", st.getId(), "firstName", st.getFirstName(), "lastName", st.getLastName(), "email", st.getEmail()))
                .collect(Collectors.toList()));
        if (s.getRubric() != null) {
            dto.put("rubric", Map.of("id", s.getRubric().getId(), "name", s.getRubric().getName()));
        }
        return dto;
    }
}
