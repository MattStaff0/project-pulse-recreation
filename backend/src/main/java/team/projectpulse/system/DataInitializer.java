package team.projectpulse.system;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import team.projectpulse.course.Course;
import team.projectpulse.course.CourseRepository;
import team.projectpulse.instructor.Instructor;
import team.projectpulse.instructor.InstructorRepository;
import team.projectpulse.rubric.Criterion;
import team.projectpulse.rubric.Rubric;
import team.projectpulse.rubric.RubricRepository;
import team.projectpulse.section.Section;
import team.projectpulse.section.SectionRepository;
import team.projectpulse.student.Student;
import team.projectpulse.student.StudentRepository;
import team.projectpulse.team.Team;
import team.projectpulse.team.TeamRepository;
import team.projectpulse.activity.Activity;
import team.projectpulse.activity.ActivityRepository;
import team.projectpulse.evaluation.PeerEvaluation;
import team.projectpulse.evaluation.PeerEvaluationRepository;
import team.projectpulse.evaluation.Rating;

import java.time.LocalDate;
import java.util.List;

@Component
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final TeamRepository teamRepository;
    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final RubricRepository rubricRepository;
    private final ActivityRepository activityRepository;
    private final PeerEvaluationRepository evaluationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CourseRepository courseRepository, SectionRepository sectionRepository,
                           TeamRepository teamRepository, StudentRepository studentRepository,
                           InstructorRepository instructorRepository, RubricRepository rubricRepository,
                           ActivityRepository activityRepository, PeerEvaluationRepository evaluationRepository,
                           PasswordEncoder passwordEncoder) {
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.teamRepository = teamRepository;
        this.studentRepository = studentRepository;
        this.instructorRepository = instructorRepository;
        this.rubricRepository = rubricRepository;
        this.activityRepository = activityRepository;
        this.evaluationRepository = evaluationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Create admin instructor
        Instructor admin = new Instructor();
        admin.setFirstName("Bingyang");
        admin.setLastName("Wei");
        admin.setEmail("b.wei@abc.edu");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setRoles("admin");
        admin.setEnabled(true);
        admin = instructorRepository.save(admin);

        // Create another instructor
        Instructor instructor2 = new Instructor();
        instructor2.setFirstName("Sarah");
        instructor2.setLastName("Johnson");
        instructor2.setEmail("s.johnson@abc.edu");
        instructor2.setPassword(passwordEncoder.encode("123456"));
        instructor2.setRoles("instructor");
        instructor2.setEnabled(true);
        instructor2 = instructorRepository.save(instructor2);

        // Create course
        Course course = new Course();
        course.setName("Senior Design 2023-2024");
        course.setDescription("Senior Design Capstone Course");
        course.setCourseAdmin(admin);
        course = courseRepository.save(course);

        // Create rubric with criteria
        Rubric rubric = new Rubric();
        rubric.setName("Peer Eval Rubric v1");
        rubric.setCourse(course);

        String[][] criteriaData = {
                {"Quality of work", "How do you rate the quality of this teammate's work? (1-10)", "10"},
                {"Productivity", "How productive is this teammate? (1-10)", "10"},
                {"Initiative", "How proactive is this teammate? (1-10)", "10"},
                {"Courtesy", "Does this teammate treat others with respect? (1-10)", "10"},
                {"Open-mindedness", "How well does this teammate handle criticism of their work? (1-10)", "10"},
                {"Engagement in meetings", "How is this teammate's performance during meetings? (1-10)", "10"}
        };

        for (String[] cd : criteriaData) {
            Criterion c = new Criterion();
            c.setName(cd[0]);
            c.setDescription(cd[1]);
            c.setMaxScore(Double.valueOf(cd[2]));
            rubric.addCriterion(c);
        }
        rubric = rubricRepository.save(rubric);

        // Create section
        Section section = new Section();
        section.setName("Section 2023-2024");
        section.setStartDate(LocalDate.of(2023, 8, 21));
        section.setEndDate(LocalDate.of(2024, 5, 1));
        section.setCourse(course);
        section.setRubric(rubric);
        section.setActiveWeeks("[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]");
        section.getInstructors().add(admin);
        section.getInstructors().add(instructor2);
        section = sectionRepository.save(section);

        // Set defaults
        admin.setDefaultCourse(course);
        admin.setDefaultSection(section);
        instructorRepository.save(admin);

        // Create teams
        Team team1 = new Team();
        team1.setName("Project Pulse Team");
        team1.setDescription("Building the peer evaluation tool");
        team1.setWebsiteUrl("https://projectpulse.example.com");
        team1.setSection(section);
        team1.getInstructors().add(admin);
        team1 = teamRepository.save(team1);

        Team team2 = new Team();
        team2.setName("CloudSync Team");
        team2.setDescription("Cloud synchronization platform");
        team2.setWebsiteUrl("https://cloudsync.example.com");
        team2.setSection(section);
        team2.getInstructors().add(instructor2);
        team2 = teamRepository.save(team2);

        // Create students
        String[][] studentData = {
                {"John", "Smith", "j.smith@abc.edu"},
                {"Emily", "Davis", "e.davis@abc.edu"},
                {"Michael", "Brown", "m.brown@abc.edu"},
                {"Sarah", "Wilson", "s.wilson@abc.edu"},
                {"David", "Taylor", "d.taylor@abc.edu"},
                {"Lisa", "Anderson", "l.anderson@abc.edu"}
        };

        Student[] students = new Student[studentData.length];
        for (int i = 0; i < studentData.length; i++) {
            Student s = new Student();
            s.setFirstName(studentData[i][0]);
            s.setLastName(studentData[i][1]);
            s.setEmail(studentData[i][2]);
            s.setPassword(passwordEncoder.encode("123456"));
            s.setRoles("student");
            s.setEnabled(true);
            s.setSection(section);
            s.setTeam(i < 3 ? team1 : team2);
            students[i] = studentRepository.save(s);
        }

        // Create sample activities for week 1
        String[] categories = {"DEVELOPMENT", "TESTING", "DESIGN", "PLANNING", "DOCUMENTATION", "COMMUNICATION"};
        String[] statuses = {"DONE", "IN_PROGRESS", "UNDER_TESTING"};
        for (int i = 0; i < students.length; i++) {
            Activity act = new Activity();
            act.setCategory(categories[i % categories.length]);
            act.setActivity("Sample activity for " + students[i].getFirstName());
            act.setDescription("Description of the activity performed during week 1");
            act.setPlannedHours(5.0 + i);
            act.setActualHours(4.0 + i);
            act.setStatus(statuses[i % statuses.length]);
            act.setWeek(1);
            act.setStudent(students[i]);
            act.setTeam(students[i].getTeam());
            activityRepository.save(act);
        }

        // Create sample peer evaluations for week 1 (team 1 students evaluate each other)
        List<Criterion> criteria = rubric.getCriteria();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                PeerEvaluation eval = new PeerEvaluation();
                eval.setEvaluator(students[i]);
                eval.setEvaluatee(students[j]);
                eval.setWeek(1);
                eval.setPublicComment("Good work, " + students[j].getFirstName() + "!");
                eval.setPrivateComment(i == j ? "" : "Needs to communicate more");

                double total = 0;
                for (Criterion criterion : criteria) {
                    Rating rating = new Rating();
                    rating.setCriterion(criterion);
                    int score = 7 + (i + j) % 4; // Scores between 7-10
                    rating.setScore(score);
                    eval.addRating(rating);
                    total += score;
                }
                eval.setTotalScore(total);
                evaluationRepository.save(eval);
            }
        }
    }
}
