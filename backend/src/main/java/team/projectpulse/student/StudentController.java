package team.projectpulse.student;

import org.springframework.web.bind.annotation.*;
import team.projectpulse.system.Result;
import team.projectpulse.system.StatusCode;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public Result findAll(@RequestParam(required = false) Long sectionId,
                          @RequestParam(required = false) Long teamId,
                          @RequestParam(required = false) String name) {
        List<Student> students;
        if (sectionId != null) students = studentService.findBySectionId(sectionId);
        else if (teamId != null) students = studentService.findByTeamId(teamId);
        else students = studentService.findAll();

        if (name != null && !name.isBlank()) {
            String lower = name.toLowerCase();
            students = students.stream()
                    .filter(s -> (s.getFirstName() + " " + s.getLastName()).toLowerCase().contains(lower))
                    .collect(Collectors.toList());
        }

        return new Result(true, StatusCode.SUCCESS, "Find students successfully",
                students.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/unassigned")
    public Result findUnassigned(@RequestParam Long sectionId) {
        return new Result(true, StatusCode.SUCCESS, "Find unassigned students",
                studentService.findUnassignedBySectionId(sectionId).stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id) {
        return new Result(true, StatusCode.SUCCESS, "Find student successfully", toDetailDto(studentService.findById(id)));
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Student update = new Student();
        update.setFirstName(body.get("firstName"));
        update.setLastName(body.get("lastName"));
        update.setEmail(body.get("email"));
        return new Result(true, StatusCode.SUCCESS, "Student updated", toDto(studentService.update(id, update)));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        studentService.delete(id);
        return new Result(true, StatusCode.SUCCESS, "Student deleted successfully");
    }

    private Map<String, Object> toDto(Student s) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", s.getId());
        dto.put("firstName", s.getFirstName());
        dto.put("lastName", s.getLastName());
        dto.put("email", s.getEmail());
        dto.put("teamId", s.getTeam() != null ? s.getTeam().getId() : null);
        dto.put("teamName", s.getTeam() != null ? s.getTeam().getName() : null);
        dto.put("sectionId", s.getSection() != null ? s.getSection().getId() : null);
        dto.put("sectionName", s.getSection() != null ? s.getSection().getName() : null);
        return dto;
    }

    private Map<String, Object> toDetailDto(Student s) {
        Map<String, Object> dto = toDto(s);
        dto.put("roles", s.getRoles());
        dto.put("enabled", s.isEnabled());
        return dto;
    }
}
