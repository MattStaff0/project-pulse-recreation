package team.projectpulse.user;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import team.projectpulse.system.Result;
import team.projectpulse.system.StatusCode;

import java.util.Map;

@RestController
@RequestMapping("${api.endpoint.base-url}/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authz.isSelf(#id) or hasRole('admin')")
    public Result findById(@PathVariable Long id) {
        PeerEvaluationUser user = userService.findById(id);
        return new Result(true, StatusCode.SUCCESS, "Find user successfully", toDto(user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authz.isSelf(#id) or hasRole('admin')")
    public Result updateProfile(@PathVariable Long id, @RequestBody Map<String, String> body) {
        PeerEvaluationUser user = userService.updateProfile(id, body.get("firstName"), body.get("lastName"), body.get("email"));
        return new Result(true, StatusCode.SUCCESS, "Update user successfully", toDto(user));
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("@authz.isSelf(#id) or hasRole('admin')")
    public Result changePassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        userService.changePassword(id, body.get("oldPassword"), body.get("newPassword"));
        return new Result(true, StatusCode.SUCCESS, "Password changed successfully");
    }

    @PostMapping("/register")
    public Result register(@RequestBody Map<String, String> body) {
        PeerEvaluationUser user = userService.register(
                body.get("email"), body.get("firstName"), body.get("lastName"),
                body.get("password"), body.get("token"));
        return new Result(true, StatusCode.SUCCESS, "Registration successful", toDto(user));
    }

    @GetMapping("/validate-invitation")
    public Result validateInvitation(@RequestParam String token) {
        UserInvitation invitation = userService.validateInvitation(token);
        Map<String, Object> data = Map.of(
                "email", invitation.getEmail(),
                "role", invitation.getRole(),
                "courseId", invitation.getCourseId() != null ? invitation.getCourseId() : "",
                "sectionId", invitation.getSectionId() != null ? invitation.getSectionId() : "",
                "accepted", invitation.isAccepted()
        );
        return new Result(true, StatusCode.SUCCESS, "Invitation valid", data);
    }

    @PostMapping("/forgot-password")
    public Result forgotPassword(@RequestBody Map<String, String> body) {
        userService.requestPasswordReset(body.get("email"));
        return new Result(true, StatusCode.SUCCESS, "If the email exists, a reset link has been sent");
    }

    @PostMapping("/reset-password")
    public Result resetPassword(@RequestBody Map<String, String> body) {
        userService.resetPassword(body.get("email"), body.get("token"), body.get("newPassword"));
        return new Result(true, StatusCode.SUCCESS, "Password reset successfully");
    }

    private Map<String, Object> toDto(PeerEvaluationUser user) {
        return Map.of(
                "id", user.getId(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "email", user.getEmail(),
                "roles", user.getRoles(),
                "enabled", user.isEnabled()
        );
    }
}
