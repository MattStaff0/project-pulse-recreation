# Known Issues & Bugs

Tracking issues discovered during testing. Each issue includes the status, description, and fix (if resolved).

---

## Issue #1: Maven Wrapper Missing

**Status:** Resolved

**Description:** The `mvnw` / `mvnw.cmd` wrapper scripts are not included in the repository. Running `./mvnw spring-boot:run` as documented in the README fails with `zsh: no such file or directory: ./mvnw`.

**Fix:** Install Maven separately (`brew install maven` on macOS) and generate the wrapper:

```bash
cd backend
mvn wrapper:wrapper
```

---

## Issue #2: Invalid Spring MVC Path Pattern

**Status:** Resolved

**Description:** The application fails to start with the error:

```
Invalid mapping pattern detected:
/**/{spring:[\w-]+}
No more pattern data allowed after {*...} or ** pattern element
```

Spring Boot 3.x does not allow `**` followed by additional path segments in the new `PathPattern` parser.

**File:** `backend/src/main/java/team/projectpulse/system/WebConfig.java`

**Fix:** Replaced the invalid `/**/{spring:[\w-]+}` pattern with explicit depth patterns:

```java
registry.addViewController("/{spring:[\\w-]+}")
        .setViewName("forward:/index.html");
registry.addViewController("/{path1:[\\w-]+}/{path2:[\\w-]+}")
        .setViewName("forward:/index.html");
registry.addViewController("/{path1:[\\w-]+}/{path2:[\\w-]+}/{path3:[\\w-]+}")
        .setViewName("forward:/index.html");
```

---

## Issue #3: README Lists Incorrect Login Credentials

**Status:** Resolved

**Description:** The README lists email addresses that do not match the actual seed data in `DataInitializer.java`. Users following the README cannot log in with the documented credentials.

**README had:**
| Role | Email |
|------------|--------------------|
| Instructor | a.jones@abc.edu |
| Student | e.johnson@abc.edu |
| Student | m.williams@abc.edu |
| Student | s.brown@abc.edu |
| Student | d.davis@abc.edu |
| Student | l.wilson@abc.edu |

**Actual emails from DataInitializer.java:**
| Role | Email |
|------------|--------------------|
| Instructor | s.johnson@abc.edu |
| Student | e.davis@abc.edu |
| Student | m.brown@abc.edu |
| Student | s.wilson@abc.edu |
| Student | d.taylor@abc.edu |
| Student | l.anderson@abc.edu |

**Fix:** Updated README to reflect the correct emails.

---

## Issue #4: Docker Port Conflict with Mailpit

**Status:** Resolved

**Description:** If a Mailpit container from a previous session is still running, `docker compose up -d` fails with:

```
Bind for 0.0.0.0:1025 failed: port is already allocated
```

**Fix:** Stop and remove the old container before starting:

```bash
docker stop <old-container-name> && docker rm <old-container-name>
docker compose up -d
```

---

## Issue #5: Windows Compatibility (Under Investigation)

**Status:** Open

**Description:** A teammate on Windows is unable to get the application running at all. Root cause is not yet determined.

**Possible causes to investigate:**

- Maven wrapper (`mvnw.cmd`) missing from repo (see Issue #1)
- Port 80 may require admin privileges on Windows
- Docker Desktop for Windows configuration differences
- Line ending issues (CRLF vs LF) in scripts
- Java/Node.js version differences

**Next steps:** Get specific error messages from the Windows teammate to narrow down the cause.

---

## Issue #6: Hibernate Table Drop Warnings on Fresh Database

**Status:** Won't Fix (Cosmetic)

**Description:** On first startup with a fresh database, Hibernate logs many WARN-level `CommandAcceptanceException` messages like:

```
Table 'project-pulse.peer_evaluation_user' doesn't exist
```

This happens because `ddl-auto: create` tries to drop tables before creating them, but they don't exist yet on a fresh database. These are harmless warnings and do not affect functionality.

---

# Use Case Validation Audit

## Scope Clarifications

- Admin users are not expected to use the self-service student pages `my-activities`, `my-evaluations`, `submit-evaluation`, or `my-eval-report`.
- Admin users are also not expected to use the instructor reporting pages `section-activities` or `section-eval-report`.
- Email delivery can stay in its current development form; production-grade email workflow is out of scope for this pass.
- This remediation branch already fixes a first batch of confirmed issues: backend role enforcement, self/ownership checks for profile and activity access, student report privacy, duplicate/no-edit peer-evaluation rules, active-week validation for peer evaluations, and student login context needed by the evaluation UI.

## Actor / Scope Summary

- Project Pulse is intended to let admins manage rubrics, sections, teams, students, instructors, invitations, and active weeks; students register, manage WARs, submit peer evaluations, and view their own report; instructors review WARs and peer-evaluation reports.
- The requirements and vision documents position the app as a replacement for manual WAR and peer-evaluation workflows, with strong actor separation and weekly/active-week business rules.
- `requirements/use_cases.md` has numbering inconsistencies inside several tables, including mismatched UC IDs in early sections and `UC-24` being labeled `UC-23` in its table.
- Cross-cutting note: the original audit found broad actor-separation gaps. This branch addresses the main backend/frontend permission issue, but several use-case-specific business rules still remain open below.

## UC-1: The Admin creates a rubric

**Status:** Needs Fixing

**Requirement Summary:**

- Admin creates a rubric with a unique name and one or more criteria.
- Each criterion needs a name, description, and positive max score.
- The flow expects validation before saving.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/RubricCreate.vue`
- Frontend: `frontend/src/apis/rubric.js`
- Backend: `backend/src/main/java/team/projectpulse/rubric/RubricController.java`
- Backend: `backend/src/main/java/team/projectpulse/rubric/RubricService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- Rubric creation exists end to end.
- The frontend only validates rubric name; criterion name/description completeness is not enforced.
- The backend only checks rubric-name uniqueness and does not validate positive decimal max scores beyond whatever the UI sends.

**Recommended Action:**

- Add backend validation for rubric payloads and criterion fields, then mirror the same rules in the form.

**Test Notes:**

- Manually create a rubric with duplicate name, empty criterion name, and zero/negative max score.

## UC-2: The Admin finds senior design sections

**Status:** Needs Fixing

**Requirement Summary:**

- Admin searches sections by criteria and sees matching results.
- Results should support section-based discovery, including empty-result handling.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/Sections.vue`
- Frontend: `frontend/src/apis/section.js`
- Backend: `backend/src/main/java/team/projectpulse/section/SectionController.java`
- Backend: `backend/src/main/java/team/projectpulse/section/SectionService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- The UI only searches client-side by section name after fetching all sections.
- The backend supports filtering by `courseId` and partial `name`, but not the full result/sort behavior described in the use case.
- No server-side sort matching the documented descending section-name ordering is enforced.

**Recommended Action:**

- Add explicit search/sort support on the backend and align the UI with the documented search fields and empty-result flow.

**Test Notes:**

- Search for an exact section name, a partial name, and a non-existent name.

## UC-3: The Admin views a senior design section

**Status:** Needs Testing

**Requirement Summary:**

- Admin views a section with dates, teams, members, instructors, unassigned students, and rubric.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/SectionDetail.vue`
- Frontend: `frontend/src/apis/section.js`
- Backend: `backend/src/main/java/team/projectpulse/section/SectionController.java`
- Backend: `backend/src/main/java/team/projectpulse/section/SectionService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- The controller returns the main fields the use case expects, including teams and unassigned students.
- The page renders those details and links into related flows.
- Server-side authorization is still missing, so any authenticated user can request section details directly.

**Recommended Action:**

- Add backend role checks and verify the detail payload against a real populated section.

**Test Notes:**

- Open a seeded section and confirm teams, students, instructors, and rubric all appear correctly.

## UC-4: The Admin creates a senior design section

**Status:** Needs Fixing

**Requirement Summary:**

- Admin creates a section with name, dates, and rubric.
- The system should validate input and prevent duplicates.
- The use case also expects rubric selection/confirmation behavior during creation.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/SectionCreate.vue`
- Frontend: `frontend/src/apis/section.js`
- Backend: `backend/src/main/java/team/projectpulse/section/SectionController.java`
- Backend: `backend/src/main/java/team/projectpulse/section/SectionService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- Basic section creation exists and duplicate names are blocked in the service.
- The form does not validate start/end date consistency or require rubric selection.
- The documented rubric-preview/edit path during section creation is not implemented.

**Recommended Action:**

- Add validation for section dates and rubric selection rules, and decide whether inline rubric duplication/editing is required or should be removed from the requirement.

**Test Notes:**

- Try creating duplicate sections, a section with end date before start date, and a section without a rubric.

## UC-5: The Admin edits a senior design section

**Status:** Needs Fixing

**Requirement Summary:**

- Admin edits section name, dates, and rubric with validation and warnings where needed.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/SectionEdit.vue`
- Frontend: `frontend/src/apis/section.js`
- Backend: `backend/src/main/java/team/projectpulse/section/SectionController.java`
- Backend: `backend/src/main/java/team/projectpulse/section/SectionService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- Edit UI and update endpoint exist.
- `SectionService.update` does not check uniqueness when renaming a section.
- There is no validation for invalid date ranges or any warning/confirmation behavior before saving.

**Recommended Action:**

- Add uniqueness and date validation on update and decide whether the confirmation step belongs in UI or requirement text.

**Test Notes:**

- Rename a section to another section's name and set invalid date ranges.

## UC-6: The Admin sets up active weeks for a senior design section

**Status:** Needs Fixing

**Requirement Summary:**

- Admin configures active weeks for a section based on its date range.
- This setup should drive WAR and peer-evaluation submission behavior.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/ActiveWeeks.vue`
- Frontend: `frontend/src/apis/section.js`
- Backend: `backend/src/main/java/team/projectpulse/section/SectionController.java`
- Backend: `backend/src/main/java/team/projectpulse/section/SectionService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- The page can save a JSON array of weeks to the section.
- The backend stores raw text with no validation that the week list matches section dates.
- Active weeks are not enforced later in peer-evaluation submission, which undermines the point of this use case.

**Recommended Action:**

- Validate active-week payloads and use them in downstream submission/reporting rules.

**Test Notes:**

- Save malformed week data and confirm whether downstream student flows honor active/inactive weeks.

## UC-7: The Admin/Instructor finds senior design teams

**Status:** Needs Fixing

**Requirement Summary:**

- Admins and instructors search teams using section/team-related criteria.
- Results should reflect the documented display and sorting rules.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/Teams.vue`
- Frontend: `frontend/src/apis/team.js`
- Backend: `backend/src/main/java/team/projectpulse/team/TeamController.java`
- Backend: `backend/src/main/java/team/projectpulse/team/TeamService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- The UI only filters client-side by team name after fetching all teams.
- The backend only supports optional `sectionId`, not section name or instructor criteria.
- Instructor scoping is absent, so instructors can enumerate all teams instead of only relevant ones.

**Recommended Action:**

- Add server-side search fields and instructor-aware filtering, then match the documented sort/display behavior.

**Test Notes:**

- Search by section, by team name, and as an instructor who should not see unrelated teams.

## UC-8: The Admin/Instructor views a senior design team

**Status:** Needs Testing

**Requirement Summary:**

- Admin or instructor views a team with name, description, website, members, and instructors.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/TeamDetail.vue`
- Frontend: `frontend/src/apis/team.js`
- Backend: `backend/src/main/java/team/projectpulse/team/TeamController.java`
- Backend: `backend/src/main/java/team/projectpulse/team/TeamService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- The detail view exposes the main fields required by the use case.
- Admin-specific team actions are hidden in the UI for instructors.
- The backend still lacks role or ownership checks on the endpoint itself.

**Recommended Action:**

- Add server-side authorization and verify the payload with both admin and instructor accounts.

**Test Notes:**

- View team details as admin and instructor, then try the endpoint with an unrelated authenticated user.

## UC-9: The Admin creates a senior design team

**Status:** Needs Fixing

**Requirement Summary:**

- Admin creates a team with unique name, description, website, and target section.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/TeamCreate.vue`
- Frontend: `frontend/src/apis/team.js`
- Backend: `backend/src/main/java/team/projectpulse/team/TeamController.java`
- Backend: `backend/src/main/java/team/projectpulse/team/TeamService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- Team creation exists and unique team names are checked on save.
- The form only requires what the frontend chooses to send and does not validate documented fields beyond basic presence.
- There is no explicit confirmation/review step before commit.

**Recommended Action:**

- Add stronger validation and align the flow with the documented review/confirmation step if that behavior is still required.

**Test Notes:**

- Create teams with duplicate names, blank required fields, and invalid section selections.

## UC-10: The Admin edits a senior design team

**Status:** Needs Fixing

**Requirement Summary:**

- Admin edits team name, description, and website while preserving uniqueness.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/TeamEdit.vue`
- Frontend: `frontend/src/apis/team.js`
- Backend: `backend/src/main/java/team/projectpulse/team/TeamController.java`
- Backend: `backend/src/main/java/team/projectpulse/team/TeamService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- The edit form and endpoint exist.
- `TeamService.update` does not enforce unique team names.
- There is no validation or warning flow around conflicting team names or malformed input.

**Recommended Action:**

- Add uniqueness and payload validation to updates.

**Test Notes:**

- Rename a team to an existing team name and try empty or malformed values.

## UC-11: The Admin invites students to join a senior design section

**Status:** Needs Fixing

**Requirement Summary:**

- Admin sends invitation emails to student addresses in a section.
- Emails should be validated, counted, and uniquely tokenized.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/InviteStudents.vue`
- Frontend: `frontend/src/apis/invitation.js`
- Backend: `backend/src/main/java/team/projectpulse/user/InvitationController.java`
- Backend: `backend/src/main/java/team/projectpulse/user/UserService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- The app sends invitation emails with unique UUID-based tokens.
- Email addresses are split on semicolons, but there is no explicit format validation or message preview/customization.
- Existing pending invitations are not checked, so the same address can be invited repeatedly before registration.

**Recommended Action:**

- Validate email format, prevent duplicate outstanding invitations, and decide whether customizable message content is required.

**Test Notes:**

- Send malformed addresses, repeated addresses, and the same address twice before registration.

## UC-12: The Admin assigns students to senior design teams

**Status:** Needs Fixing

**Requirement Summary:**

- Admin assigns students to teams and confirms the final assignment.
- Students should be associated to one team and notified.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/AssignStudents.vue`
- Frontend: `frontend/src/apis/team.js`
- Backend: `backend/src/main/java/team/projectpulse/team/TeamController.java`
- Backend: `backend/src/main/java/team/projectpulse/team/TeamService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- Assignment UI exists and updates `student.team`.
- The service does not verify that the student and team belong to the same section.
- No notification or final confirmation step is implemented.

**Recommended Action:**

- Enforce same-section assignment rules and add notification/confirmation if the requirement stands.

**Test Notes:**

- Attempt cross-section assignment after creating multiple sections.

## UC-13: The Admin removes a student from a senior design team

**Status:** Needs Fixing

**Requirement Summary:**

- Admin removes a student from a team so the student can be reassigned later.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/AssignStudents.vue`
- Frontend: `frontend/src/pages/admin/TeamDetail.vue`
- Backend: `backend/src/main/java/team/projectpulse/team/TeamController.java`
- Backend: `backend/src/main/java/team/projectpulse/team/TeamService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- Removal sets `student.team` to `null`, which matches the basic association change.
- There is no notification implementation.
- The backend allows the operation for any authenticated caller and does not validate business context.

**Recommended Action:**

- Add role enforcement and notification behavior, then verify reassignment still works cleanly.

**Test Notes:**

- Remove a student, confirm they appear as unassigned, then reassign them to a new team.

## UC-14: The Admin deletes a senior design team

**Status:** Needs Fixing

**Requirement Summary:**

- Admin deletes a team, first detaching members/instructors and also removing related WARs and peer evaluations.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/TeamDetail.vue`
- Frontend: `frontend/src/apis/team.js`
- Backend: `backend/src/main/java/team/projectpulse/team/TeamService.java`
- Backend: `backend/src/main/java/team/projectpulse/team/Team.java`
- Other: `requirements/use_cases.md`

**Findings:**

- The service clears student team assignments and instructor assignments before deleting the team.
- It does not delete associated activities or peer evaluations as the use case requires.
- Notifications to affected users are not implemented.

**Recommended Action:**

- Define the intended deletion semantics for activities/evaluations, implement them transactionally, and add notifications.

**Test Notes:**

- Delete a team that already has activities/evaluations and confirm whether the delete succeeds and what data remains.

## UC-15: The Admin/Instructor finds students

**Status:** Needs Fixing

**Requirement Summary:**

- Admins and instructors search students by name, email, section, and team criteria.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/Students.vue`
- Frontend: `frontend/src/apis/student.js`
- Backend: `backend/src/main/java/team/projectpulse/student/StudentController.java`
- Backend: `backend/src/main/java/team/projectpulse/student/StudentService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- The UI only supports client-side name filtering.
- The backend supports `sectionId`, `teamId`, and name matching, but not email/section-name/team-name criteria.
- Instructors are not scoped to their own students on the backend.

**Recommended Action:**

- Expand search criteria support and enforce instructor visibility rules.

**Test Notes:**

- Search by email, by section/team, and as an instructor with limited ownership.

## UC-16: The Admin/Instructor views a student

**Status:** Needs Fixing

**Requirement Summary:**

- Admins and instructors view student details, including team/section context plus WAR and peer-evaluation access.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/StudentDetail.vue`
- Frontend: `frontend/src/pages/instructor/StudentActivities.vue`
- Frontend: `frontend/src/pages/instructor/StudentEvalReport.vue`
- Backend: `backend/src/main/java/team/projectpulse/student/StudentController.java`
- Other: `requirements/use_cases.md`

**Findings:**

- The base student detail page only shows identity, section, and team.
- WAR and peer-evaluation drill-down buttons are only shown when `isInstructor`, not for admins.
- Backend access control is absent, so detail endpoints are not truly restricted to admin/instructor actors.

**Recommended Action:**

- Decide whether admins should also see WAR/evaluation drill-down and enforce authorization on the backend.

**Test Notes:**

- View the same student as admin and instructor and compare available actions.

## UC-17: The Admin deletes a student

**Status:** Needs Fixing

**Requirement Summary:**

- Admin physically deletes a student and also removes their related WARs and peer evaluations.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/Students.vue`
- Frontend: `frontend/src/apis/student.js`
- Backend: `backend/src/main/java/team/projectpulse/student/StudentService.java`
- Backend: `backend/src/main/java/team/projectpulse/activity/Activity.java`
- Backend: `backend/src/main/java/team/projectpulse/evaluation/PeerEvaluation.java`

**Findings:**

- The service directly deletes the student record.
- There is no explicit cleanup of activities or peer evaluations despite the requirement calling for physical delete of associated data.
- With current entity mappings, this is likely to fail on foreign-key constraints or leave integrity to implicit database behavior.

**Recommended Action:**

- Implement transactional cleanup or explicit cascade rules for student-owned activities and evaluations before deletion.

**Test Notes:**

- Delete a student who already has seeded activities/evaluations and verify the database outcome.

## UC-18: The Admin invites instructors to register an account

**Status:** Needs Fixing

**Requirement Summary:**

- Admin sends instructor invitation emails with unique registration links and validated email input.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/InviteInstructors.vue`
- Frontend: `frontend/src/apis/invitation.js`
- Backend: `backend/src/main/java/team/projectpulse/user/InvitationController.java`
- Backend: `backend/src/main/java/team/projectpulse/user/UserService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- Invitation sending exists and uses unique tokens.
- The UI in `InviteInstructors.vue` calls the generic invitation endpoint and does not offer message customization or validation feedback.
- Duplicate outstanding invitations are not prevented.

**Recommended Action:**

- Add input validation and duplicate-pending-invitation protection.

**Test Notes:**

- Send repeated invitations to the same email and malformed email values.

## UC-19: The Admin assigns instructors to senior design teams

**Status:** Needs Fixing

**Requirement Summary:**

- Admin assigns instructors to teams, preserving the team/instructor relationship and supporting BR-1.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/AssignInstructors.vue`
- Frontend: `frontend/src/apis/team.js`
- Backend: `backend/src/main/java/team/projectpulse/team/TeamService.java`
- Backend: `backend/src/main/java/team/projectpulse/instructor/Instructor.java`
- Other: `requirements/use_cases.md`

**Findings:**

- Assignment exists by adding instructors to the team collection.
- The service does not verify section compatibility or any prerequisite beyond IDs existing.
- BR-1 is not enforced anywhere; teams can exist or end up with zero instructors.

**Recommended Action:**

- Enforce at-least-one-instructor and section/assignment rules in the service layer.

**Test Notes:**

- Assign and remove instructors around the same team and confirm BR-1 behavior.

## UC-20: The Admin removes an instructor from a senior design team

**Status:** Needs Fixing

**Requirement Summary:**

- Admin removes an instructor from a team and the system should maintain valid team supervision.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/AssignInstructors.vue`
- Frontend: `frontend/src/pages/admin/TeamDetail.vue`
- Backend: `backend/src/main/java/team/projectpulse/team/TeamService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- Removal exists and simply drops the matching instructor from the list.
- The service allows removing the last instructor, violating BR-1.
- Required notification behavior is not implemented.

**Recommended Action:**

- Block removal that would leave a team without instructors and add notification handling.

**Test Notes:**

- Remove instructors until only one remains and confirm whether the system stops the final removal.

## UC-21: The Admin finds instructors

**Status:** Needs Fixing

**Requirement Summary:**

- Admin searches instructors by name, team, and status and sees ordered results.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/Instructors.vue`
- Frontend: `frontend/src/apis/instructor.js`
- Backend: `backend/src/main/java/team/projectpulse/instructor/InstructorController.java`
- Backend: `backend/src/main/java/team/projectpulse/instructor/InstructorService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- The UI only filters by name client-side.
- The backend supports name and status filtering, but not team-name search.
- No documented sort order is enforced.

**Recommended Action:**

- Add server-side team-name filtering and align sorting with the requirement.

**Test Notes:**

- Filter by active/deactivated and by team assignment.

## UC-22: The Admin views an instructor

**Status:** Needs Fixing

**Requirement Summary:**

- Admin views instructor details, supervised teams, and status, organized in a useful structure.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/InstructorDetail.vue`
- Frontend: `frontend/src/apis/instructor.js`
- Backend: `backend/src/main/java/team/projectpulse/instructor/InstructorController.java`
- Backend: `backend/src/main/java/team/projectpulse/instructor/InstructorService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- The detail page shows basic identity, status, and teams.
- The requirement says supervised teams should be organized by section, but the implementation returns flat `teams` and `sections`.
- Backend role enforcement is still missing.

**Recommended Action:**

- Reshape the payload/UI by section if that requirement still stands and add backend authorization.

**Test Notes:**

- View an instructor with multiple teams across sections.

## UC-23: The Admin deactivate an instructor

**Status:** Needs Fixing

**Requirement Summary:**

- Admin deactivates an instructor account so the instructor loses system access.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/Instructors.vue`
- Frontend: `frontend/src/apis/instructor.js`
- Backend: `backend/src/main/java/team/projectpulse/instructor/InstructorController.java`
- Backend: `backend/src/main/java/team/projectpulse/instructor/InstructorService.java`
- Backend: `backend/src/main/java/team/projectpulse/security/CustomUserDetails.java`

**Findings:**

- Deactivation flips `enabled` to `false`, and Spring Security respects `isEnabled()` during authentication.
- The requirement says the admin enters a reason, but no reason is collected or stored.
- Any authenticated caller can hit the deactivate endpoint because backend role restrictions are missing.

**Recommended Action:**

- Add server-side admin authorization and decide whether deactivation reasons are required domain data or should be removed from the use case.

**Test Notes:**

- Deactivate an instructor, then verify login is blocked.

## UC-24: The Admin reactivate an instructor

**Status:** Needs Fixing

**Requirement Summary:**

- Admin reactivates an instructor account and the instructor regains access.

**Relevant Code:**

- Frontend: `frontend/src/pages/admin/Instructors.vue`
- Frontend: `frontend/src/apis/instructor.js`
- Backend: `backend/src/main/java/team/projectpulse/instructor/InstructorController.java`
- Backend: `backend/src/main/java/team/projectpulse/instructor/InstructorService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- Reactivation sets `enabled` back to `true`.
- The use case says the instructor should be notified; there is no notification implementation.
- Backend role enforcement is missing here as well.

**Recommended Action:**

- Add admin-only authorization and implement or explicitly drop the notification requirement.

**Test Notes:**

- Reactivate a disabled instructor and verify login works again.

## UC-25: The Student sets up a student account

**Status:** Needs Testing

**Requirement Summary:**

- Student follows an invitation link, registers, and is redirected to login.

**Relevant Code:**

- Frontend: `frontend/src/pages/auth/Register.vue`
- Frontend: `frontend/src/apis/login.js`
- Backend: `backend/src/main/java/team/projectpulse/user/UserController.java`
- Backend: `backend/src/main/java/team/projectpulse/user/UserService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- Invitation validation, registration, token consumption, and redirect-to-login behavior are implemented.
- The service correctly attaches invited students to the invited section when `sectionId` exists.
- The flow still needs end-to-end confirmation with real invitation emails and reused-token handling.

**Recommended Action:**

- Run a full invite-to-register test and verify the accepted-token guard works after first use.

**Test Notes:**

- Invite a new student, register, then revisit the same link.

## UC-26: The Student edits an account

**Status:** Needs Fixing

**Requirement Summary:**

- Student updates profile details and can change password with validation.

**Relevant Code:**

- Frontend: `frontend/src/pages/Profile.vue`
- Frontend: `frontend/src/apis/user.js`
- Backend: `backend/src/main/java/team/projectpulse/user/UserController.java`
- Backend: `backend/src/main/java/team/projectpulse/user/UserService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- Profile edit and password change UI exists.
- The backend lets any authenticated caller update any user ID or change any user password if they know the current password for that account.
- Email uniqueness and other profile validations are not handled cleanly in the service layer.

**Recommended Action:**

- Tie profile/password actions to the authenticated principal or add admin-only exceptions explicitly.

**Test Notes:**

- Attempt profile changes on another user's ID and try changing email to one already in use.

## UC-27: The Student manages activities in a Weekly Activity Report (WAR)

**Status:** Needs Fixing

**Requirement Summary:**

- Student chooses a week, then adds, edits, or deletes activities in that WAR.
- Future-week restrictions and activity validation should be enforced.

**Relevant Code:**

- Frontend: `frontend/src/pages/student/MyActivities.vue`
- Frontend: `frontend/src/apis/activity.js`
- Backend: `backend/src/main/java/team/projectpulse/activity/ActivityController.java`
- Backend: `backend/src/main/java/team/projectpulse/activity/ActivityService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- Add/edit/delete flows exist in the student UI.
- The page hardcodes weeks `1..15` and does not prevent selecting future weeks.
- The backend does not verify that the caller owns the activity or that the selected week is allowed.

**Recommended Action:**

- Enforce ownership and week rules in the backend and derive valid week choices from section context instead of hardcoding.

**Test Notes:**

- Create activities in future weeks and try editing/deleting another student's activity by ID through the API.

## UC-28: The Student submits a peer evaluation for the previous week

**Status:** Needs Fixing

**Requirement Summary:**

- Student evaluates every teammate, including self, for the previous week only.
- Scores must be integers and peer evaluation is tied to business rules BR-3 and BR-4.

**Relevant Code:**

- Frontend: `frontend/src/pages/student/SubmitEvaluation.vue`
- Frontend: `frontend/src/apis/evaluation.js`
- Backend: `backend/src/main/java/team/projectpulse/evaluation/PeerEvaluationController.java`
- Backend: `backend/src/main/java/team/projectpulse/evaluation/PeerEvaluationService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- The page uses `userInfoStore.userInfo.teamId`, but login payloads do not include `teamId`, so teammate lookup is unreliable.
- The flow does not enforce "previous week only", active-week rules, or one-submission-per-evaluator/evaluatee/week.
- The update endpoint allows editing after submission even though BR-3 says completed peer evaluations cannot be edited.

**Recommended Action:**

- Include team/section context in login state, enforce BR-3 and BR-4 on the backend, and prevent duplicate submissions.

**Test Notes:**

- Log in as a student, open submit-evaluation, and verify teammate/rubric loading; then attempt duplicate and out-of-window submissions.

## UC-29: The Student views her own peer evaluation report

**Status:** Needs Fixing

**Requirement Summary:**

- Student views her own weekly peer-evaluation report with average criterion scores, public comments, and overall grade only.

**Relevant Code:**

- Frontend: `frontend/src/pages/student/MyEvalReport.vue`
- Frontend: `frontend/src/apis/evaluation.js`
- Backend: `backend/src/main/java/team/projectpulse/evaluation/PeerEvaluationController.java`
- Backend: `backend/src/main/java/team/projectpulse/evaluation/PeerEvaluationService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- The student-facing report excludes private comments and evaluator names, which matches BR-5.
- The backend endpoint accepts arbitrary `studentId`, so any authenticated user can fetch someone else's report directly.
- Week selection is hardcoded rather than defaulting to the previous active week.

**Recommended Action:**

- Restrict student report access to the owner unless the caller is an authorized instructor/admin path.

**Test Notes:**

- As a student, try requesting another student's report through the API.

## UC-30: The Instructor sets up an instructor account

**Status:** Needs Fixing

**Requirement Summary:**

- Invited instructor registers, confirms details, and is redirected to login.
- The use case includes middle initial and password confirmation behavior.

**Relevant Code:**

- Frontend: `frontend/src/pages/auth/Register.vue`
- Frontend: `frontend/src/apis/login.js`
- Backend: `backend/src/main/java/team/projectpulse/user/UserController.java`
- Backend: `backend/src/main/java/team/projectpulse/user/UserService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- The same registration flow supports instructors and students based on invitation role.
- There is no middle-initial field even though the use case lists one.
- There is no confirmation/review step before final registration.

**Recommended Action:**

- Decide whether middle initial and explicit confirmation are real requirements; if so, implement them in the shared registration flow.

**Test Notes:**

- Invite an instructor and complete the registration flow end to end.

## UC-31: The Instructor generates a peer evaluation report of the entire senior design section

**Status:** Needs Fixing

**Requirement Summary:**

- Instructor generates a section-wide weekly peer-evaluation report with grades, comments, and no-submit visibility.

**Relevant Code:**

- Frontend: `frontend/src/pages/instructor/SectionEvalReport.vue`
- Frontend: `frontend/src/apis/evaluation.js`
- Backend: `backend/src/main/java/team/projectpulse/evaluation/PeerEvaluationController.java`
- Backend: `backend/src/main/java/team/projectpulse/evaluation/PeerEvaluationService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- A section-wide weekly report exists and averages total scores per student.
- The report does not show who failed to submit peer evaluations for that week.
- Section access is not restricted to instructors/admins with appropriate ownership.

**Recommended Action:**

- Add no-submit reporting and authorization checks, and verify the displayed columns against the requirement example.

**Test Notes:**

- Generate a report for a week where at least one student has no evaluation submission.

## UC-32: The Instructor/Student generates a WAR report of a senior design team

**Status:** Needs Fixing

**Requirement Summary:**

- Instructor or student generates a weekly WAR report for a senior design team.
- The report should show activity rows and who did not submit.

**Relevant Code:**

- Frontend: `frontend/src/pages/instructor/SectionActivities.vue`
- Frontend: `frontend/src/pages/instructor/TeamActivities.vue`
- Frontend: `frontend/src/apis/activity.js`
- Backend: `backend/src/main/java/team/projectpulse/activity/ActivityController.java`
- Other: `requirements/use_cases.md`

**Findings:**

- Instructor team WAR pages exist and can list team activities by week.
- There is no student-accessible team WAR route even though the use case includes students.
- The report does not indicate which team members failed to submit WARs for that week.

**Recommended Action:**

- Decide whether student team-report access is required and add missing-submission reporting.

**Test Notes:**

- Compare team roster to returned activities for a week with partial submissions.

## UC-33: The Instructor generates a peer evaluation report of a student

**Status:** Needs Fixing

**Requirement Summary:**

- Instructor generates a student report over a period, not just a single week.
- The report should include week, grade, comments, and detailed evaluation drill-down.

**Relevant Code:**

- Frontend: `frontend/src/pages/instructor/StudentEvalReport.vue`
- Frontend: `frontend/src/apis/evaluation.js`
- Backend: `backend/src/main/java/team/projectpulse/evaluation/PeerEvaluationController.java`
- Backend: `backend/src/main/java/team/projectpulse/evaluation/PeerEvaluationService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- The current implementation only supports one week at a time.
- Detailed evaluation rows include private comments and evaluator names, which is appropriate for instructor use.
- The documented period-based report generation is not implemented.

**Recommended Action:**

- Add start-week/end-week period reporting or revise the requirement if only single-week drill-down is needed.

**Test Notes:**

- Attempt to review the same student across multiple weeks without changing the page one week at a time.

## UC-34: The Instructor generates a WAR report of the student

**Status:** Needs Fixing

**Requirement Summary:**

- Instructor generates a student WAR report across a period, grouped by active week.

**Relevant Code:**

- Frontend: `frontend/src/pages/instructor/StudentActivities.vue`
- Frontend: `frontend/src/apis/activity.js`
- Backend: `backend/src/main/java/team/projectpulse/activity/ActivityController.java`
- Backend: `backend/src/main/java/team/projectpulse/activity/ActivityService.java`
- Other: `requirements/use_cases.md`

**Findings:**

- The current page only shows one selected week at a time.
- The requirement expects a period-based report grouped by week.
- Backend activity queries also only support a single optional `week`, not a period.

**Recommended Action:**

- Add period-based activity reporting or revise the requirement to match the simpler weekly view.

**Test Notes:**

- Try to review a student's contributions over multiple weeks from one report screen.

## Use Case Audit Summary

| Use Case | Title                                                                                 | Status        | Notes                                                                        |
| -------- | ------------------------------------------------------------------------------------- | ------------- | ---------------------------------------------------------------------------- |
| UC-1     | The Admin creates a rubric                                                            | Needs Fixing  | Creation exists; validation is incomplete.                                   |
| UC-2     | The Admin finds senior design sections                                                | Needs Fixing  | Search/sort only partially implemented.                                      |
| UC-3     | The Admin views a senior design section                                               | Needs Testing | Detail view exists; auth still needs verification.                           |
| UC-4     | The Admin creates a senior design section                                             | Needs Fixing  | Basic create works; rubric/date flow is incomplete.                          |
| UC-5     | The Admin edits a senior design section                                               | Needs Fixing  | Update lacks uniqueness/date validation.                                     |
| UC-6     | The Admin sets up active weeks for a senior design section                            | Needs Fixing  | Setup exists; downstream enforcement is missing.                             |
| UC-7     | The Admin/Instructor finds senior design teams                                        | Needs Fixing  | Search criteria and instructor scoping are incomplete.                       |
| UC-8     | The Admin/Instructor views a senior design team                                       | Needs Testing | Team detail exists; backend auth is missing.                                 |
| UC-9     | The Admin creates a senior design team                                                | Needs Fixing  | Create exists; validation/review flow is partial.                            |
| UC-10    | The Admin edits a senior design team                                                  | Needs Fixing  | Rename uniqueness is not enforced.                                           |
| UC-11    | The Admin invites students to join a senior design section                            | Needs Fixing  | Invitation flow exists; validation and duplicate-pending checks are missing. |
| UC-12    | The Admin assigns students to senior design teams                                     | Needs Fixing  | Assignment works; business rules and notifications do not.                   |
| UC-13    | The Admin removes a student from a senior design team                                 | Needs Fixing  | Removal works; notification/auth gaps remain.                                |
| UC-14    | The Admin deletes a senior design team                                                | Needs Fixing  | Related-data deletion rules are not implemented.                             |
| UC-15    | The Admin/Instructor finds students                                                   | Needs Fixing  | Search is much narrower than required.                                       |
| UC-16    | The Admin/Instructor views a student                                                  | Needs Fixing  | Core detail exists; related WAR/eval access is incomplete.                   |
| UC-17    | The Admin deletes a student                                                           | Needs Fixing  | Associated data cleanup is not implemented safely.                           |
| UC-18    | The Admin invites instructors to register an account                                  | Needs Fixing  | Same core invitation gaps as student invites.                                |
| UC-19    | The Admin assigns instructors to senior design teams                                  | Needs Fixing  | BR-1 and section rules are not enforced.                                     |
| UC-20    | The Admin removes an instructor from a senior design team                             | Needs Fixing  | Can violate BR-1 and does not notify.                                        |
| UC-21    | The Admin finds instructors                                                           | Needs Fixing  | Team-name filtering and sort behavior are missing.                           |
| UC-22    | The Admin views an instructor                                                         | Needs Fixing  | Detail shape does not match section-grouped requirement.                     |
| UC-23    | The Admin deactivate an instructor                                                    | Needs Fixing  | Disable works; reason/auth requirements are missing.                         |
| UC-24    | The Admin reactivate an instructor                                                    | Needs Fixing  | Reactivation works; notification/auth requirements are missing.              |
| UC-25    | The Student sets up a student account                                                 | Needs Testing | Registration flow looks plausible and needs end-to-end confirmation.         |
| UC-26    | The Student edits an account                                                          | Needs Fixing  | Profile/password endpoints are not scoped to the authenticated user.         |
| UC-27    | The Student manages activities in a Weekly Activity Report (WAR)                      | Needs Fixing  | CRUD exists; future-week and ownership rules do not.                         |
| UC-28    | The Student submits a peer evaluation for the previous week                           | Needs Fixing  | Major business rules are not enforced.                                       |
| UC-29    | The Student views her own peer evaluation report                                      | Needs Fixing  | Student-facing report exists but is not ownership-restricted.                |
| UC-30    | The Instructor sets up an instructor account                                          | Needs Fixing  | Shared registration flow misses some required fields/steps.                  |
| UC-31    | The Instructor generates a peer evaluation report of the entire senior design section | Needs Fixing  | Section report exists but misses no-submit visibility and auth controls.     |
| UC-32    | The Instructor/Student generates a WAR report of a senior design team                 | Needs Fixing  | Instructor-only weekly report exists; student/absence behavior does not.     |
| UC-33    | The Instructor generates a peer evaluation report of a student                        | Needs Fixing  | Single-week drill-down exists; period report does not.                       |
| UC-34    | The Instructor generates a WAR report of the student                                  | Needs Fixing  | Single-week view exists; period report does not.                             |
