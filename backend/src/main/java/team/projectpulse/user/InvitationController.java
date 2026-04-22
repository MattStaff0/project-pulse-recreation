package team.projectpulse.user;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import team.projectpulse.system.Result;
import team.projectpulse.system.StatusCode;

import java.util.Map;

@RestController
@RequestMapping("${api.endpoint.base-url}/invitations")
@PreAuthorize("hasRole('admin')")
public class InvitationController {

    private final UserService userService;

    public InvitationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/students")
    public Result inviteStudents(@RequestBody Map<String, Object> body) {
        String emailsStr = (String) body.get("emails");
        Long courseId = body.get("courseId") != null ? Long.valueOf(body.get("courseId").toString()) : null;
        Long sectionId = body.get("sectionId") != null ? Long.valueOf(body.get("sectionId").toString()) : null;
        String senderName = (String) body.getOrDefault("senderName", "Admin");
        String[] emails = emailsStr.split(";");
        userService.sendInvitations(emails, "student", courseId, sectionId, senderName);
        return new Result(true, StatusCode.SUCCESS, "Invitation emails sent to " + emails.length + " students");
    }

    @PostMapping("/instructors")
    public Result inviteInstructors(@RequestBody Map<String, Object> body) {
        String emailsStr = (String) body.get("emails");
        Long courseId = body.get("courseId") != null ? Long.valueOf(body.get("courseId").toString()) : null;
        Long sectionId = body.get("sectionId") != null ? Long.valueOf(body.get("sectionId").toString()) : null;
        String senderName = (String) body.getOrDefault("senderName", "Admin");
        String[] emails = emailsStr.split(";");
        userService.sendInvitations(emails, "instructor", courseId, sectionId, senderName);
        return new Result(true, StatusCode.SUCCESS, "Invitation emails sent to " + emails.length + " instructors");
    }
}
