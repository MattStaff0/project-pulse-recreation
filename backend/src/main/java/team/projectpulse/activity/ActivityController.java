package team.projectpulse.activity;

import org.springframework.web.bind.annotation.*;
import team.projectpulse.student.Student;
import team.projectpulse.student.StudentService;
import team.projectpulse.system.Result;
import team.projectpulse.system.StatusCode;
import team.projectpulse.team.Team;
import team.projectpulse.team.TeamService;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/activities")
public class ActivityController {

    private final ActivityService activityService;
    private final StudentService studentService;
    private final TeamService teamService;

    public ActivityController(ActivityService activityService, StudentService studentService, TeamService teamService) {
        this.activityService = activityService;
        this.studentService = studentService;
        this.teamService = teamService;
    }

    @GetMapping
    public Result findAll(@RequestParam(required = false) Long studentId,
                          @RequestParam(required = false) Long teamId,
                          @RequestParam(required = false) Integer week) {
        List<Activity> activities;
        if (studentId != null && week != null) {
            activities = activityService.findByStudentAndWeek(studentId, week);
        } else if (teamId != null && week != null) {
            activities = activityService.findByTeamAndWeek(teamId, week);
        } else if (studentId != null) {
            activities = activityService.findByStudentId(studentId);
        } else if (teamId != null) {
            activities = activityService.findByTeamId(teamId);
        } else {
            activities = List.of();
        }
        return new Result(true, StatusCode.SUCCESS, "Find activities successfully",
                activities.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id) {
        return new Result(true, StatusCode.SUCCESS, "Find activity successfully", toDto(activityService.findById(id)));
    }

    @PostMapping
    public Result create(@RequestBody Map<String, Object> body) {
        Activity activity = new Activity();
        activity.setCategory((String) body.get("category"));
        activity.setActivity((String) body.get("activity"));
        activity.setDescription((String) body.get("description"));
        activity.setPlannedHours(body.get("plannedHours") != null ? Double.valueOf(body.get("plannedHours").toString()) : null);
        activity.setActualHours(body.get("actualHours") != null ? Double.valueOf(body.get("actualHours").toString()) : null);
        activity.setStatus((String) body.get("status"));
        activity.setComments((String) body.get("comments"));
        activity.setWeek(body.get("week") != null ? Integer.valueOf(body.get("week").toString()) : null);

        if (body.get("studentId") != null) {
            Student student = studentService.findById(Long.valueOf(body.get("studentId").toString()));
            activity.setStudent(student);
            if (student.getTeam() != null) {
                activity.setTeam(student.getTeam());
            }
        }
        return new Result(true, StatusCode.SUCCESS, "Activity created successfully", toDto(activityService.save(activity)));
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Activity update = new Activity();
        update.setCategory((String) body.get("category"));
        update.setActivity((String) body.get("activity"));
        update.setDescription((String) body.get("description"));
        update.setPlannedHours(body.get("plannedHours") != null ? Double.valueOf(body.get("plannedHours").toString()) : null);
        update.setActualHours(body.get("actualHours") != null ? Double.valueOf(body.get("actualHours").toString()) : null);
        update.setStatus((String) body.get("status"));
        update.setComments((String) body.get("comments"));
        return new Result(true, StatusCode.SUCCESS, "Activity updated successfully", toDto(activityService.update(id, update)));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        activityService.delete(id);
        return new Result(true, StatusCode.SUCCESS, "Activity deleted successfully");
    }

    private Map<String, Object> toDto(Activity a) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", a.getId());
        dto.put("category", a.getCategory());
        dto.put("activity", a.getActivity());
        dto.put("description", a.getDescription());
        dto.put("plannedHours", a.getPlannedHours());
        dto.put("actualHours", a.getActualHours());
        dto.put("status", a.getStatus());
        dto.put("comments", a.getComments());
        dto.put("week", a.getWeek());
        dto.put("studentId", a.getStudent() != null ? a.getStudent().getId() : null);
        dto.put("studentName", a.getStudent() != null ? a.getStudent().getFirstName() + " " + a.getStudent().getLastName() : null);
        dto.put("teamId", a.getTeam() != null ? a.getTeam().getId() : null);
        dto.put("teamName", a.getTeam() != null ? a.getTeam().getName() : null);
        dto.put("createdAt", a.getCreatedAt());
        dto.put("updatedAt", a.getUpdatedAt());
        return dto;
    }
}
