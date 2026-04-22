package team.projectpulse.security;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.projectpulse.system.Result;
import team.projectpulse.system.StatusCode;

import team.projectpulse.student.Student;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.endpoint.base-url}/users")
public class AuthController {

    private final JwtProvider jwtProvider;

    public AuthController(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/login")
    public Result login(Authentication authentication) {
        String token = jwtProvider.createToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        var user = userDetails.getUser();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("firstName", user.getFirstName());
        userInfo.put("lastName", user.getLastName());
        userInfo.put("email", user.getEmail());
        userInfo.put("roles", user.getRoles());
        userInfo.put("enabled", user.isEnabled());

        if (user instanceof Student student) {
            userInfo.put("teamId", student.getTeam() != null ? student.getTeam().getId() : null);
            userInfo.put("sectionId", student.getSection() != null ? student.getSection().getId() : null);
        }

        Map<String, Object> loginResult = new HashMap<>();
        loginResult.put("userInfo", userInfo);
        loginResult.put("token", token);

        return new Result(true, StatusCode.SUCCESS, "Login successful", loginResult);
    }
}
