package team.projectpulse.team;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import team.projectpulse.security.AuthorizationService;
import team.projectpulse.section.Section;
import team.projectpulse.section.SectionService;
import team.projectpulse.system.Result;
import team.projectpulse.system.StatusCode;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/teams")
public class TeamController {

    private final TeamService teamService;
    private final SectionService sectionService;
    private final AuthorizationService authorizationService;

    public TeamController(TeamService teamService, SectionService sectionService, AuthorizationService authorizationService) {
        this.teamService = teamService;
        this.sectionService = sectionService;
        this.authorizationService = authorizationService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'instructor')")
    public Result findAll(@RequestParam(required = false) Long sectionId) {
        List<Team> teams = sectionId != null ? teamService.findBySectionId(sectionId) : teamService.findAll();
        return new Result(true, StatusCode.SUCCESS, "Find teams successfully",
                teams.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id) {
        authorizationService.requireCanReadTeam(id);
        return new Result(true, StatusCode.SUCCESS, "Find team successfully", toDetailDto(teamService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public Result create(@RequestBody Map<String, Object> body) {
        Team team = new Team();
        team.setName((String) body.get("name"));
        team.setDescription((String) body.get("description"));
        team.setWebsiteUrl((String) body.get("websiteUrl"));
        if (body.get("sectionId") != null) {
            Section section = sectionService.findById(Long.valueOf(body.get("sectionId").toString()));
            team.setSection(section);
        }
        return new Result(true, StatusCode.SUCCESS, "Team created successfully", toDto(teamService.save(team)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public Result update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Team update = new Team();
        update.setName((String) body.get("name"));
        update.setDescription((String) body.get("description"));
        update.setWebsiteUrl((String) body.get("websiteUrl"));
        return new Result(true, StatusCode.SUCCESS, "Team updated successfully", toDto(teamService.update(id, update)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public Result delete(@PathVariable Long id) {
        teamService.delete(id);
        return new Result(true, StatusCode.SUCCESS, "Team deleted successfully");
    }

    @PostMapping("/{teamId}/students/{studentId}")
    @PreAuthorize("hasRole('admin')")
    public Result assignStudent(@PathVariable Long teamId, @PathVariable Long studentId) {
        teamService.assignStudentToTeam(teamId, studentId);
        return new Result(true, StatusCode.SUCCESS, "Student assigned to team successfully");
    }

    @DeleteMapping("/{teamId}/students/{studentId}")
    @PreAuthorize("hasRole('admin')")
    public Result removeStudent(@PathVariable Long teamId, @PathVariable Long studentId) {
        teamService.removeStudentFromTeam(teamId, studentId);
        return new Result(true, StatusCode.SUCCESS, "Student removed from team successfully");
    }

    @PostMapping("/{teamId}/instructors/{instructorId}")
    @PreAuthorize("hasRole('admin')")
    public Result assignInstructor(@PathVariable Long teamId, @PathVariable Long instructorId) {
        teamService.assignInstructorToTeam(teamId, instructorId);
        return new Result(true, StatusCode.SUCCESS, "Instructor assigned to team successfully");
    }

    @DeleteMapping("/{teamId}/instructors/{instructorId}")
    @PreAuthorize("hasRole('admin')")
    public Result removeInstructor(@PathVariable Long teamId, @PathVariable Long instructorId) {
        teamService.removeInstructorFromTeam(teamId, instructorId);
        return new Result(true, StatusCode.SUCCESS, "Instructor removed from team successfully");
    }

    private Map<String, Object> toDto(Team t) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", t.getId());
        dto.put("name", t.getName());
        dto.put("description", t.getDescription());
        dto.put("websiteUrl", t.getWebsiteUrl());
        dto.put("sectionId", t.getSection() != null ? t.getSection().getId() : null);
        dto.put("sectionName", t.getSection() != null ? t.getSection().getName() : null);
        dto.put("instructorNames", t.getInstructors().stream()
                .map(i -> i.getFirstName() + " " + i.getLastName())
                .sorted()
                .collect(Collectors.toList()));
        dto.put("numberOfStudents", t.getStudents().size());
        return dto;
    }

    private Map<String, Object> toDetailDto(Team t) {
        Map<String, Object> dto = toDto(t);
        dto.put("students", t.getStudents().stream().map(s -> Map.of(
                "id", s.getId(), "firstName", s.getFirstName(), "lastName", s.getLastName(), "email", s.getEmail()
        )).collect(Collectors.toList()));
        dto.put("instructors", t.getInstructors().stream().map(i -> Map.of(
                "id", i.getId(), "firstName", i.getFirstName(), "lastName", i.getLastName()
        )).collect(Collectors.toList()));
        return dto;
    }
}
