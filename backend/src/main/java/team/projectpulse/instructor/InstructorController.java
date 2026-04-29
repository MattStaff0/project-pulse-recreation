package team.projectpulse.instructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import team.projectpulse.system.Result;
import team.projectpulse.system.StatusCode;
import team.projectpulse.user.UserService;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/instructors")
@PreAuthorize("hasRole('admin')")
public class InstructorController {

    private final InstructorService instructorService;
    private final UserService userService;

    public InstructorController(InstructorService instructorService, UserService userService) {
        this.instructorService = instructorService;
        this.userService = userService;
    }

    @GetMapping
    public Result findAll(@RequestParam(required = false) String name,
                          @RequestParam(required = false) String status) {
        List<Instructor> instructors = instructorService.findAll();
        if (name != null && !name.isBlank()) {
            String lower = name.toLowerCase();
            instructors = instructors.stream()
                    .filter(i -> (i.getFirstName() + " " + i.getLastName()).toLowerCase().contains(lower))
                    .collect(Collectors.toList());
        }
        if ("active".equalsIgnoreCase(status)) {
            instructors = instructors.stream().filter(Instructor::isEnabled).collect(Collectors.toList());
        } else if ("deactivated".equalsIgnoreCase(status)) {
            instructors = instructors.stream().filter(i -> !i.isEnabled()).collect(Collectors.toList());
        }
        return new Result(true, StatusCode.SUCCESS, "Find instructors successfully",
                instructors.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id) {
        return new Result(true, StatusCode.SUCCESS, "Find instructor successfully", toDetailDto(instructorService.findById(id)));
    }

    @PostMapping("/invite")
    public Result invite(@RequestBody Map<String, Object> body) {
        String emailsStr = (String) body.get("emails");
        Long courseId = body.get("courseId") != null ? Long.valueOf(body.get("courseId").toString()) : null;
        Long sectionId = body.get("sectionId") != null ? Long.valueOf(body.get("sectionId").toString()) : null;
        String senderName = (String) body.getOrDefault("senderName", "Admin");
        String[] emails = emailsStr.split(";");
        userService.sendInvitations(emails, "instructor", courseId, sectionId, senderName);
        return new Result(true, StatusCode.SUCCESS, "Invitation emails sent to " + emails.length + " instructors");
    }

    @PutMapping("/{id}/deactivate")
    public Result deactivate(@PathVariable Long id) {
        instructorService.deactivate(id);
        return new Result(true, StatusCode.SUCCESS, "Instructor deactivated");
    }

    @PutMapping("/{id}/reactivate")
    public Result reactivate(@PathVariable Long id) {
        instructorService.reactivate(id);
        return new Result(true, StatusCode.SUCCESS, "Instructor reactivated");
    }

    private Map<String, Object> toDto(Instructor i) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", i.getId());
        dto.put("firstName", i.getFirstName());
        dto.put("lastName", i.getLastName());
        dto.put("email", i.getEmail());
        dto.put("enabled", i.isEnabled());
        dto.put("teams", i.getTeams().stream().map(t -> Map.of("id", t.getId(), "name", t.getName())).collect(Collectors.toList()));
        return dto;
    }

    private Map<String, Object> toDetailDto(Instructor i) {
        Map<String, Object> dto = toDto(i);
        dto.put("sections", i.getSections().stream().map(s -> Map.of("id", s.getId(), "name", s.getName())).collect(Collectors.toList()));
        return dto;
    }
}
