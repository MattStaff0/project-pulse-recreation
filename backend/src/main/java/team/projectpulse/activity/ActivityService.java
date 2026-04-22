package team.projectpulse.activity;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.security.AuthorizationService;
import team.projectpulse.system.exception.ObjectNotFoundException;

import java.util.List;

@Service
@Transactional
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final AuthorizationService authorizationService;

    public ActivityService(ActivityRepository activityRepository, AuthorizationService authorizationService) {
        this.activityRepository = activityRepository;
        this.authorizationService = authorizationService;
    }

    public List<Activity> findByStudentAndWeek(Long studentId, Integer week) {
        return activityRepository.findByStudentIdAndWeek(studentId, week);
    }

    public List<Activity> findByTeamAndWeek(Long teamId, Integer week) {
        return activityRepository.findByTeamIdAndWeek(teamId, week);
    }

    public List<Activity> findByStudentId(Long studentId) {
        return activityRepository.findByStudentId(studentId);
    }

    public List<Activity> findByTeamId(Long teamId) {
        return activityRepository.findByTeamId(teamId);
    }

    public Activity findById(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("activity", id));
    }

    public Activity save(Activity activity) {
        if (activity.getStudent() == null) {
            throw new IllegalArgumentException("Student is required");
        }
        authorizationService.requireCanCreateActivityForStudent(activity.getStudent().getId());
        return activityRepository.save(activity);
    }

    public Activity update(Long id, Activity update) {
        Activity existing = findById(id);
        authorizationService.requireCanManageActivity(existing);
        existing.setCategory(update.getCategory());
        existing.setActivity(update.getActivity());
        existing.setDescription(update.getDescription());
        existing.setPlannedHours(update.getPlannedHours());
        existing.setActualHours(update.getActualHours());
        existing.setStatus(update.getStatus());
        existing.setComments(update.getComments());
        return activityRepository.save(existing);
    }

    public void delete(Long id) {
        Activity existing = findById(id);
        authorizationService.requireCanManageActivity(existing);
        activityRepository.deleteById(id);
    }
}
