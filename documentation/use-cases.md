# Use Cases

## Notes

This markdown version normalizes the numbering around the `UC-1` through `UC-34` use-case list from the source artifact. The original document contains a few heading-number mismatches, but the canonical sequence below follows the published use-case list.

## Use Case Template Reference

The original artifact defines the following common use-case elements:

- `Use Case ID and Name`: unique identifier and concise value-oriented name
- `Primary and Secondary Actors`: external people or systems involved
- `Trigger`: the event or action that begins the use case
- `Description`: the purpose and result of the use case
- `Preconditions`: conditions that must be true before execution
- `Postconditions`: system state after successful completion
- `Main Success Scenario`: the normal flow
- `Extensions`: alternative flows and exceptions
- `Priority`
- `Frequency of Use`
- `Business Rules`
- `Associated Information`
- `Assumptions`

## Use Case List

### Admin

| ID | Name |
| --- | --- |
| UC-1 | Create a rubric |
| UC-2 | Find senior design sections |
| UC-3 | View a senior design section |
| UC-4 | Create a senior design section |
| UC-5 | Edit a senior design section |
| UC-6 | Set up active weeks for a senior design section |
| UC-7 | Find senior design teams |
| UC-8 | View a senior design team |
| UC-9 | Create a senior design team |
| UC-10 | Edit a senior design team |
| UC-11 | Invite students to join a senior design section |
| UC-12 | Assign students to senior design teams |
| UC-13 | Remove a student from a senior design team |
| UC-14 | Delete a senior design team |
| UC-15 | Find students |
| UC-16 | View a student |
| UC-17 | Delete a student |
| UC-18 | Invite instructors to register an account |
| UC-19 | Assign instructors to senior design teams |
| UC-20 | Remove an instructor from a senior design team |
| UC-21 | Find instructors |
| UC-22 | View an instructor |
| UC-23 | Deactivate an instructor |
| UC-24 | Reactivate an instructor |

### Student

| ID | Name |
| --- | --- |
| UC-25 | Set up a student account |
| UC-26 | Edit an account |
| UC-27 | Manage activities in a weekly activity report |
| UC-28 | Submit a peer evaluation for the previous week |
| UC-29 | View her own peer evaluation report |

### Instructor

| ID | Name |
| --- | --- |
| UC-30 | Set up an instructor account |
| UC-31 | Generate a peer evaluation report of the entire senior design section |
| UC-32 | Generate a WAR report of a senior design team |
| UC-33 | Generate a peer evaluation report of a student |
| UC-34 | Generate a WAR report of the student |

## Detailed Use Cases

## UC-1: Create a Rubric

- Primary actor: `Admin`
- Trigger: The admin indicates a new rubric should be created.
- Description: The admin creates a rubric so students can assess peer performance effectively.
- Preconditions:
  - `PRE-1`: The admin is logged into the system.
- Postconditions:
  - `POST-1`: The new rubric is stored in the system.
- Main success scenario:
  1. The admin indicates creation of a new rubric.
  2. The system asks for rubric details.
  3. The admin enters rubric details and finishes.
  4. The system validates the input.
  5. The system displays the rubric and asks for confirmation.
  6. The admin confirms or returns to editing.
  7. The system saves the rubric and confirms creation.
- Extensions:
  - `4a`: Input validation rule violation.
  - `4a1`: The system alerts the admin to the error and its location.
  - `4a2`: The admin corrects the mistake and returns to validation.
- Priority: `High`
- Frequency of use: `1 user, 1 usage per year`
- Associated information:
  - Rubric name must be unique.
  - Each criterion has:
    - name
    - description
    - max score
  - Max score must be positive and may be decimal.
  - Example criteria:
    - Quality of work
    - Productivity
    - Initiative
    - Courtesy
    - Open-mindedness
    - Engagement in meetings
  - The admin may cancel before submission.

## UC-2: Find Senior Design Sections

- Primary actor: `Admin`
- Trigger: The admin indicates that she wants to find senior design sections.
- Description: The admin searches for sections matching specific criteria.
- Preconditions:
  - `PRE-1`: The admin is logged into the system.
- Postconditions:
  - `POST-1`: A list of matching sections is displayed; it may be empty.
- Main success scenario:
  1. The admin starts the search.
  2. The system asks for search values.
  3. The admin enters one or more search values.
  4. The system finds matching sections.
  5. The system displays matching sections.
- Extensions:
  - `4a`: No matching sections are found.
  - `4a1`: The system alerts the admin.
  - `4a2`: The admin may create a section, terminate, or search again.
- Priority: `High`
- Frequency of use: `1 user, average of 5 usages per week`
- Associated information:
  - Search criteria:
    - `section name` (string, optional)
  - Search results display strategy:
    - section name
    - team names
  - Sort:
    - section name descending
    - team names ascending

## UC-3: View a Senior Design Section

- Primary actor: `Admin`
- Trigger: The admin indicates that she wants to view a section's details.
- Description: The admin views a specific section to better understand it.
- Preconditions:
  - `PRE-1`: The admin is logged into the system.
- Postconditions:
  - `POST-1`: The section details are displayed.
- Main success scenario:
  1. The admin indicates she wants to view a section.
  2. She finds sections through `UC-2`.
  3. She selects a specific section.
  4. The system retrieves and displays its details.
  5. The admin views the details.
- Priority: `High`
- Frequency of use: `1 user, average of 5 usages per week`
- Associated information:
  - Details shown include:
    - section name
    - start date
    - end date
    - teams, team members, and instructors
    - instructors not assigned to a team
    - students not assigned to a team
    - rubric used

## UC-4: Create a Senior Design Section

- Primary actor: `Admin`
- Trigger: The admin indicates creation of a new senior design section.
- Description: The admin creates a section so students can later be invited into it.
- Preconditions:
  - `PRE-1`: The admin is logged into the system.
- Postconditions:
  - `POST-1`: The new section is stored in the system.
- Main success scenario:
  1. The admin starts section creation.
  2. The system asks for section details.
  3. The admin enters section details.
  4. The system asks the admin to choose a rubric.
  5. The admin selects an existing rubric.
  6. The system displays the rubric criteria.
  7. The admin confirms use of that rubric.
  8. The system validates the inputs.
  9. The system checks for duplicate sections.
  10. The system displays the new section details for confirmation.
  11. The admin confirms or returns to editing.
  12. The system saves the section and confirms creation.
- Extensions:
  - `5a`: Rubric does not exist or the admin wants to create a new rubric.
  - `5a1`: The admin uses `UC-1: Create a rubric`.
  - `5a2`: Flow returns to rubric confirmation.
  - `7a`: The admin edits rubric criteria.
  - `7a1`: The admin edits name, description, or max score for a criterion.
  - `7a2`: The system saves the changes.
  - `7a3`: Flow returns to rubric confirmation.
  - `8a`: Input validation rule violation.
  - `9a`: Possible duplicate section found.
- Priority: `High`
- Frequency of use: `1 user, 1 usage per year`
- Associated information:
  - Details:
    - section name, for example `Section 2023-2024`
    - start date
    - end date
  - Editing a rubric duplicates the original rubric first, then edits the copy.
  - Duplication detection:
    - section name is unique
  - The admin may cancel before submission.
- Related use cases:
  - The admin may arrive here after not finding a section in `UC-2`.

## UC-5: Edit a Senior Design Section

- Primary actor: `Admin`
- Trigger: The admin indicates she wants to change an existing section.
- Description: The admin updates section information so it remains correct and current.
- Preconditions:
  - `PRE-1`: The admin is logged into the system.
- Postconditions:
  - `POST-1`: Section changes are stored.
- Main success scenario:
  1. The admin indicates she wants to edit a section.
  2. She views the section through `UC-3`.
  3. She chooses to change its details.
  4. The system presents editable fields.
  5. The admin makes changes and finishes.
  6. The system validates the changes and warnings.
  7. The admin acknowledges warnings.
  8. The system displays updated details for confirmation.
  9. The admin confirms or continues editing.
  10. The system saves the changes and applies effects.
- Extensions:
  - `6a`: Input validation rule violation.
- Priority: `High`
- Frequency of use: `1 user, average of 1 usage per year`
- Associated information:
  - Editable details:
    - section name
    - start and end date
    - rubric used
  - The admin may cancel before submission.

## UC-6: Set Up Active Weeks For a Senior Design Section

- Primary actor: `Admin`
- Trigger: The admin indicates she wants to configure active weeks for a section.
- Description: The admin marks which weeks require WAR and peer-evaluation submissions.
- Preconditions:
  - `PRE-1`: The admin is logged into the system.
  - `PRE-2`: At least one senior design section exists.
- Postconditions:
  - `POST-1`: Active weeks are stored for the section.
- Main success scenario:
  1. The admin starts active-week setup.
  2. The system displays all weeks in the section based on start and end dates.
  3. The admin marks weeks during which students do not need to submit.
  4. The system displays the resulting active weeks and asks for confirmation.
  5. The admin confirms or returns to editing.
  6. The system saves the setup.
- Priority: `High`
- Frequency of use: `1 user, 1 usage per year`
- Business rules:
  - `BR-2`
- Associated information:
  - The admin may cancel before submission.

## UC-7: Find Senior Design Teams

- Primary actor: `Admin`, `Instructor`
- Trigger: The user indicates she wants to find teams.
- Description: The user searches for teams matching given criteria.
- Preconditions:
  - `PRE-1`: The user is logged into the system.
- Postconditions:
  - `POST-1`: A list of matching teams is displayed; it may be empty.
- Main success scenario:
  1. The user starts the search.
  2. The system asks for search criteria.
  3. The user enters search values.
  4. The system finds matching teams.
  5. The system displays them.
- Extensions:
  - `4a`: No matching teams found.
- Priority: `High`
- Frequency of use: `2 users, average of 2 usages per week`
- Associated information:
  - Search criteria:
    - section id
    - section name
    - team name
    - instructor
  - Search result fields:
    - team name
    - team description
    - team website URL
    - team members
    - instructors
  - Sort:
    - section name descending
    - team name ascending

## UC-8: View a Senior Design Team

- Primary actor: `Admin`, `Instructor`
- Trigger: The user indicates she wants to view a team.
- Description: The user views team details to better understand the team.
- Preconditions:
  - `PRE-1`: The user is logged into the system.
- Postconditions:
  - `POST-1`: Team details are displayed.
- Main success scenario:
  1. The user indicates she wants to view a team.
  2. She finds teams through `UC-7`.
  3. She selects a specific team.
  4. The system retrieves and displays the team details.
  5. The user views the details.
- Priority: `High`
- Frequency of use: `2 users, average of 2 usages per week`
- Associated information:
  - Details shown include:
    - team name
    - team description
    - team website URL
    - team members
    - instructors

## UC-9: Create a Senior Design Team

- Primary actor: `Admin`
- Trigger: The admin indicates creation of a new team.
- Description: The admin creates a team so students can later be assigned to it.
- Preconditions:
  - `PRE-1`: The admin is logged into the system.
- Postconditions:
  - `POST-1`: The new team is stored in the system.
- Main success scenario:
  1. The admin starts team creation for a section.
  2. The system asks for team details.
  3. The admin enters those details.
  4. The system validates the input.
  5. The system checks for duplicates.
  6. The system displays details for confirmation.
  7. The admin confirms or returns to editing.
  8. The system saves the team.
- Extensions:
  - `4a`: Input validation rule violation.
  - `5a`: Duplicate team found.
- Priority: `High`
- Frequency of use: `1 user, 5-10 usages per year`
- Associated information:
  - Details:
    - team name
    - team description
    - team website URL
  - Duplication detection:
    - team name must be unique
  - The admin may cancel before submission.

## UC-10: Edit a Senior Design Team

- Primary actor: `Admin`
- Trigger: The admin indicates she wants to change an existing team.
- Description: The admin updates team details.
- Preconditions:
  - `PRE-1`: The admin is logged into the system.
- Postconditions:
  - `POST-1`: Team changes are stored.
- Main success scenario:
  1. The admin indicates she wants to edit a team.
  2. She views the team through `UC-8`.
  3. She chooses to change team details.
  4. The system presents editable details.
  5. The admin makes changes and finishes.
  6. The system validates the changes and warnings.
  7. The admin acknowledges warnings.
  8. The system displays updated details for confirmation.
  9. The admin confirms or continues editing.
  10. The system saves the team changes.
- Extensions:
  - `6a`: Team name conflict.
- Priority: `High`
- Frequency of use: `1 user, average of 6 usages per year`
- Associated information:
  - Editable details:
    - team name
    - team description
    - team website URL
  - Team names must be unique.
  - The admin may cancel before submission.

## UC-11: Invite Students To Join a Senior Design Section

- Primary actor: `Admin`
- Secondary actor: `Student`
- Trigger: The admin indicates she wants to invite students.
- Description: The admin sends invitation emails so students can join a section.
- Preconditions:
  - `PRE-1`: The admin is logged into the system.
- Postconditions:
  - `POST-1`: Invitation emails are sent.
- Main success scenario:
  1. The admin starts the invitation flow.
  2. The system asks for student email addresses.
  3. The admin provides email addresses.
  4. The system validates the input format.
  5. The system displays the number of emails.
  6. The system displays the email message.
  7. The admin confirms sending or returns to editing.
  8. The system sends one email to each address.
- Extensions:
  - `4a`: Input validation rule violation.
  - `6a`: The admin personalizes the default email message.
- Priority: `High`
- Frequency of use: `1 user, 1 usage per year`
- Associated information:
  - Email addresses are separated by semicolons.
  - Spaces between addresses are ignored.
  - The default email invites the student to register using a unique link.
  - The admin may cancel before submission.
- Related use cases:
  - The student proceeds to `UC-25`.

## UC-12: Assign Students To Senior Design Teams

- Primary actor: `Admin`
- Secondary actor: `Student`
- Trigger: The admin indicates she wants to assign students to teams.
- Description: The admin assigns students so they can begin reporting and peer evaluation.
- Preconditions:
  - `PRE-1`: Teams are created.
  - `PRE-2`: Students have set up accounts.
  - `PRE-3`: The admin is logged into the system.
- Postconditions:
  - `POST-1`: Every student is associated with one team.
- Main success scenario:
  1. The admin starts student assignment.
  2. The system displays lists of teams and students.
  3. The admin assigns students to teams until complete.
  4. The system displays assignment information for confirmation.
  5. The admin confirms.
  6. The system notifies relevant actors.
- Extensions:
  - `4a`: The admin notices a wrong team assignment and corrects it.
- Priority: `High`
- Frequency of use: `1 user, 1 usage per year`
- Associated information:
  - Students are notified about their team assignment.
  - The admin may cancel before submission.

## UC-13: Remove a Student From a Senior Design Team

- Primary actor: `Admin`
- Secondary actor: `Student`
- Trigger: The admin indicates she wants to remove a student from a team.
- Description: The admin removes a student so the student can be reassigned.
- Preconditions:
  - `PRE-1`: Teams are created.
  - `PRE-2`: Students have set up accounts.
  - `PRE-3`: The admin is logged into the system.
  - `PRE-4`: Students have been assigned to teams.
- Postconditions:
  - `POST-1`: The student is removed from the team.
- Main success scenario:
  1. The admin starts the removal flow.
  2. She views the team through `UC-8`.
  3. She removes a student from the team.
  4. The system displays updated assignments for confirmation.
  5. The admin confirms.
  6. The system notifies relevant actors.
- Extensions:
  - `5a`: The admin notices a wrong removal and corrects it.
- Priority: `Low`
- Frequency of use: `Rare, 1 user, 1 usage per year`
- Associated information:
  - The removed student is notified.
  - The admin may cancel before submission.
- Related use cases:
  - The admin may immediately assign the student to a new team.

## UC-14: Delete a Senior Design Team

- Primary actor: `Admin`
- Secondary actors: `Student`, `Instructor`
- Trigger: The admin indicates she wants to delete an existing team.
- Description: The admin deletes a team and its dependent data according to system rules.
- Preconditions:
  - `PRE-1`: The admin is logged into the system.
  - `PRE-2`: At least one senior design team exists.
- Postconditions:
  - `POST-1`: The team is deleted according to the defined deletion strategy.
- Main success scenario:
  1. The admin starts the delete flow.
  2. She views the team through `UC-8`.
  3. She chooses delete.
  4. The system warns about consequences and requests confirmation.
  5. The admin confirms.
  6. The system deletes the team.
  7. The system notifies relevant actors.
- Priority: `Low`
- Frequency of use: `Rare, 1 user, 1 usage per year`
- Associated information:
  - Data integrity:
    - deleting a team removes assigned students and instructors from the team first
    - deleting a team also deletes associated WARs and peer evaluations
  - Deletion strategy:
    - physical delete
    - not recoverable
  - Students and instructors of the deleted team are notified.
  - The admin may cancel before submission.
- Related use cases:
  - The admin may then assign students and instructors again via `UC-12` and `UC-19`.

## UC-15: Find Students

- Primary actor: `Admin`, `Instructor`
- Trigger: The user indicates she wants to find students.
- Description: The user searches for students that match specified criteria.
- Preconditions:
  - `PRE-1`: The user is logged into the system.
- Postconditions:
  - `POST-1`: A list of matching students is displayed; it may be empty.
- Main success scenario:
  1. The user starts the search.
  2. The system asks for search values.
  3. The user enters one or more values.
  4. The system finds matching students.
  5. The system displays them.
- Extensions:
  - `4a`: No matching students found.
- Priority: `High`
- Frequency of use: `2 users, average of 2 usages per week`
- Associated information:
  - Search criteria:
    - first name
    - last name
    - email
    - section name
    - team name
    - section id
    - team id
  - Search results:
    - first name
    - last name
    - team name
  - Sort:
    - section name descending
    - student last name ascending

## UC-16: View a Student

- Primary actor: `Admin`, `Instructor`
- Trigger: The user indicates she wants to view a student.
- Description: The user views details of a specific student.
- Preconditions:
  - `PRE-1`: The user is logged into the system.
- Postconditions:
  - `POST-1`: The student details are displayed.
- Main success scenario:
  1. The user indicates she wants to view a student.
  2. She finds students through `UC-15`.
  3. She selects a specific student.
  4. The system retrieves and displays student details.
  5. The user views the details.
- Priority: `High`
- Frequency of use: `1 user, average of 10 usages per week`
- Associated information:
  - Details shown include:
    - first name
    - last name
    - section name
    - team name
    - peer evaluations
    - WARs

## UC-17: Delete a Student

- Primary actor: `Admin`
- Secondary actor: `Student`
- Trigger: The admin indicates she wants to delete a student.
- Description: The admin deletes a student, for example when the student drops the section.
- Preconditions:
  - `PRE-1`: The admin is logged into the system.
  - `PRE-2`: At least one student exists in the system.
- Postconditions:
  - `POST-1`: The student is deleted according to the deletion strategy.
- Main success scenario:
  1. The admin starts the delete flow.
  2. She views the student through `UC-16`.
  3. She chooses delete.
  4. The system warns about consequences and asks for confirmation.
  5. The admin confirms.
  6. The system deletes the student.
- Priority: `Low`
- Frequency of use: `Rare, 1 user, 1 usage per year`
- Associated information:
  - Deleting a student also deletes associated WARs and peer evaluations.
  - Deletion is a physical delete and cannot be recovered.
  - The admin may cancel before submission.

## UC-18: Invite Instructors To Register an Account

- Primary actor: `Admin`
- Secondary actor: `Instructor`
- Trigger: The admin indicates she wants to invite instructors.
- Description: The admin sends instructor registration emails.
- Preconditions:
  - `PRE-1`: The admin is logged into the system.
- Postconditions:
  - `POST-1`: Invitation emails are sent to instructors.
- Main success scenario:
  1. The admin starts the invitation flow.
  2. The system asks for instructor email addresses.
  3. The admin provides them.
  4. The system validates the format.
  5. The system displays the email count.
  6. The system displays the email message.
  7. The admin confirms sending or returns to editing.
  8. The system sends the emails.
- Extensions:
  - `4a`: Input validation rule violation.
  - `6a`: The admin personalizes the default email message.
- Priority: `High`
- Frequency of use: `1 user, 1 usage per year`
- Associated information:
  - Email format uses semicolon-separated addresses.
  - Spaces are ignored.
  - Each invitation link must be unique.
  - The admin may cancel before submission.
- Related use cases:
  - The instructor proceeds to `UC-30`.

## UC-19: Assign Instructors To Senior Design Teams

- Primary actor: `Admin`
- Secondary actor: `Instructor`
- Trigger: The admin indicates she wants to assign instructors to teams.
- Description: The admin assigns instructors so they can supervise teams.
- Preconditions:
  - `PRE-1`: Teams are created.
  - `PRE-2`: Instructors have set up accounts.
  - `PRE-3`: The admin is logged into the system.
- Postconditions:
  - `POST-1`: Instructors are associated with teams.
- Main success scenario:
  1. The admin starts assignment.
  2. The system displays teams and instructors.
  3. The admin assigns one or more instructors to each team.
  4. The system displays assignments for confirmation.
  5. The admin confirms.
  6. The system notifies relevant actors.
- Extensions:
  - `4a`: The admin notices a wrong assignment and corrects it.
- Priority: `High`
- Frequency of use: `1 user, 1 usage per year`
- Business rules:
  - `BR-1`
- Associated information:
  - Instructors are notified about their team assignments.
  - The admin may cancel before submission.
- Assumptions:
  - The instructor must be assigned to the section of the team first. The source marks this as `TODO`.

## UC-20: Remove an Instructor From a Senior Design Team

- Primary actor: `Admin`
- Secondary actor: `Instructor`
- Trigger: The admin indicates she wants to remove an instructor from a team.
- Description: The admin removes an instructor so that instructor no longer supervises that team.
- Preconditions:
  - `PRE-1`: Teams are created.
  - `PRE-2`: Instructors have set up accounts.
  - `PRE-3`: The admin is logged into the system.
  - `PRE-4`: Instructors have been assigned to teams.
- Postconditions:
  - `POST-1`: The instructor is removed from the team.
- Main success scenario:
  1. The admin starts the removal flow.
  2. She views the team through `UC-8`.
  3. She removes an instructor from the team.
  4. The system displays updated assignments for confirmation.
  5. The admin confirms.
  6. The system notifies relevant actors.
- Extensions:
  - `5a`: The admin notices a wrong removal and corrects it.
- Priority: `Low`
- Frequency of use: `Rare, 1 user, 1 usage per year`
- Business rules:
  - `BR-1`
- Associated information:
  - The removed instructor is notified.
  - The admin may cancel before submission.
- Related use cases:
  - The admin may immediately assign that instructor to a new team.

## UC-21: Find Instructors

- Primary actor: `Admin`
- Trigger: The admin indicates she wants to find instructors.
- Description: The admin searches for instructors matching specific criteria.
- Preconditions:
  - `PRE-1`: The admin is logged into the system.
- Postconditions:
  - `POST-1`: A list of matching instructors is displayed; it may be empty.
- Main success scenario:
  1. The admin starts the search.
  2. The system asks for search values.
  3. The admin enters search values.
  4. The system finds matching instructors.
  5. The system displays them.
- Extensions:
  - `4a`: No matching instructors are found.
- Priority: `High`
- Frequency of use: `1 user, 3 usages per year`
- Associated information:
  - Search criteria:
    - first name
    - last name
    - team name
    - status (`Active` or `Deactivated`)
  - Search results:
    - first name
    - last name
    - team name
    - status
  - Sort:
    - academic year reverse chronological
    - instructor last name ascending

## UC-22: View an Instructor

- Primary actor: `Admin`
- Trigger: The admin indicates she wants to view an instructor.
- Description: The admin views instructor details to better understand the instructor's role and status.
- Preconditions:
  - `PRE-1`: The admin is logged into the system.
- Postconditions:
  - `POST-1`: Instructor details are displayed.
- Main success scenario:
  1. The admin indicates she wants to view an instructor.
  2. She finds instructors through `UC-21`.
  3. She chooses a specific instructor.
  4. The system retrieves and displays the instructor details.
  5. The admin views them.
- Priority: `Medium`
- Frequency of use: `1 user, 5 usages per year`
- Associated information:
  - Details shown include:
    - first name
    - last name
    - supervised teams
    - status
  - Supervised teams are organized by section name.

## UC-23: Deactivate an Instructor

- Primary actor: `Admin`
- Trigger: The admin indicates she wants to deactivate an instructor.
- Description: The admin deactivates an instructor so that instructor loses system access.
- Preconditions:
  - `PRE-1`: The admin is logged into the system.
  - `PRE-2`: At least one instructor exists in the system.
- Postconditions:
  - `POST-1`: The instructor account is deactivated.
- Main success scenario:
  1. The admin starts the deactivation flow.
  2. She views the instructor through `UC-22`.
  3. She chooses deactivate and enters a reason.
  4. The system warns about consequences and asks for confirmation.
  5. The admin confirms.
  6. The system deactivates the instructor.
- Priority: `Low`
- Frequency of use: `Rare, 1 user, 1 usage per year`
- Associated information:
  - Consequence:
    - the instructor loses access
    - instructor data remains in the system
  - Deactivation is reversible.
  - The admin may cancel before submission.

## UC-24: Reactivate an Instructor

- Primary actor: `Admin`
- Trigger: The admin indicates she wants to reactivate an instructor.
- Description: The admin restores access to a previously deactivated instructor.
- Preconditions:
  - `PRE-1`: The admin is logged into the system.
  - `PRE-2`: At least one deactivated instructor exists in the system.
- Postconditions:
  - `POST-1`: The instructor account is reactivated.
- Main success scenario:
  1. The admin starts the reactivation flow.
  2. She views the instructor through `UC-22`.
  3. She chooses reactivate.
  4. The system asks for confirmation.
  5. The admin confirms.
  6. The system reactivates the instructor and notifies her.
- Priority: `Low`
- Frequency of use: `Rare, 1 user, 1 usage per year`
- Associated information:
  - The admin may cancel before submission.

## UC-25: Set Up a Student Account

- Primary actor: `Student`
- Trigger: The student clicks the registration link from the invitation email.
- Description: The student creates an account to join a section and begin using the system.
- Preconditions:
  - `PRE-1`: An invitation email has been sent.
- Postconditions:
  - `POST-1`: The student account is set up.
- Main success scenario:
  1. The student clicks the registration link.
  2. The system opens a registration page and asks for account details.
  3. The student enters details and finishes.
  4. The system validates the input.
  5. The system displays entered details for confirmation.
  6. The student confirms or returns to editing.
  7. The system saves the account.
  8. The system redirects the student to the login page.
- Extensions:
  - `2a`: The account is already set up.
  - `4a`: Input validation rule violation.
- Priority: `High`
- Frequency of use: `Approximately 35-40 users, 1 usage per year`
- Associated information:
  - Details:
    - first name
    - last name
    - email
    - password
  - The student may cancel before submission.

## UC-26: Edit an Account

- Primary actor: `Student`
- Trigger: The student indicates she wants to change her account details.
- Description: The student corrects mistakes or updates account information such as password.
- Preconditions:
  - `PRE-1`: The student is logged into the system.
- Postconditions:
  - `POST-1`: Account changes are stored.
- Main success scenario:
  1. The student indicates she wants to edit her account.
  2. The system displays account details.
  3. The student chooses to edit.
  4. The system presents editable details.
  5. The student makes changes and finishes.
  6. The system validates the changes and warnings.
  7. The student acknowledges warnings.
  8. The system displays updated details for confirmation.
  9. The student confirms or continues editing.
  10. The system saves the changes.
- Extensions:
  - `6a`: Input validation rule violation.
- Priority: `High`
- Frequency of use: `Rare, approximately 35-40 users, 1 usage per year`
- Associated information:
  - Editable fields:
    - first name
    - last name
    - email
  - The student may cancel before submission.

## UC-27: Manage Activities In a Weekly Activity Report

- Primary actor: `Student`
- Trigger: The student indicates she wants to manage WAR activities.
- Description: The student adds, edits, or deletes activities in a WAR.
- Preconditions:
  - `PRE-1`: The student is logged into the system.
- Postconditions:
  - `POST-1`: A new activity is added
  - `POST-2`: An existing activity is edited
  - `POST-3`: An existing activity is deleted
- Main success scenario:
  1. The student starts WAR activity management.
  2. The system asks the student to select an active week.
  3. The student selects an active week, but not a future active week.
  4. The system displays the student's existing activities and available operations.
  5. The student chooses one operation:
     - add
     - edit
     - delete
  6. If adding:
     - the student enters activity details
     - the system validates input
     - the student confirms
     - the system adds the activity
  7. If editing:
     - the student edits an existing activity
     - the system validates the change
     - the system displays the updated details for confirmation
     - the student confirms
     - the system saves the change
  8. If deleting:
     - the student selects an existing activity
     - the system asks for confirmation
     - the student confirms
     - the system deletes the activity
- Extensions:
  - `8a`: Input validation rule violation during add.
  - `13a`: Input validation rule violation during edit.
- Priority: `High`
- Frequency of use: `Approximately 35-40 users, average of 3 usages per week`
- Associated information:
  - Activity fields:
    - category
    - activity
    - description
    - planned hours
    - actual hours
    - status
  - Categories:
    - `DEVELOPMENT`
    - `TESTING`
    - `BUGFIX`
    - `COMMUNICATION`
    - `DOCUMENTATION`
    - `DESIGN`
    - `PLANNING`
    - `LEARNING`
    - `DEPLOYMENT`
    - `SUPPORT`
    - `MISCELLANEOUS`
  - Status values:
    - `In progress`
    - `Under testing`
    - `Done`
  - All above properties are editable.
  - The student may cancel before submission.

## UC-28: Submit a Peer Evaluation For the Previous Week

- Primary actor: `Student`
- Trigger: The student indicates she wants to submit a peer evaluation for the previous week.
- Description: The student evaluates every teammate so feedback and assessment can be recorded.
- Preconditions:
  - `PRE-1`: The student is logged into the system.
- Postconditions:
  - `POST-1`: The peer evaluation is stored in the system.
- Main success scenario:
  1. The student starts peer evaluation submission.
  2. The system asks for evaluations of every team member.
  3. The student evaluates each team member, including self, and finishes.
  4. The system validates the input.
  5. The system displays the evaluation for confirmation and submission.
  6. The student confirms or returns to editing.
  7. The system saves the peer evaluation.
- Extensions:
  - `4a`: Input validation rule violation.
- Priority: `High`
- Frequency of use: `Approximately 35-40 users, 1 usage per week`
- Business rules:
  - `BR-3`
  - `BR-4`
- Associated information:
  - Every team member must be evaluated.
  - Scores must be integers.
  - Each student record includes:
    - criterion scores
    - public comments
    - private comments
  - Private comments are visible to the instructor only.
  - Public comments are sent to the student being evaluated.
  - The student may cancel before submission.

## UC-29: View Her Own Peer Evaluation Report

- Primary actor: `Student`
- Trigger: The student indicates she wants to view her own peer evaluation report on demand.
- Description: The student views a report to understand how teammates assess her work.
- Preconditions:
  - `PRE-1`: The student is logged into the system.
- Postconditions:
  - `POST-1`: The report details are displayed.
- Main success scenario:
  1. The student indicates report generation.
  2. The system asks for report parameters.
  3. The student enters parameters.
  4. The system validates parameters.
  5. The system generates and displays the report.
  6. The system delivers the report in the specified format.
- Extensions:
  - `4a`: Input validation rule violation.
  - `5a`: No data is returned.
- Priority: `High`
- Frequency of use: `Approximately 35-40 users, average of 1 usage per week`
- Business rules:
  - `BR-5`
- Associated information:
  - Report parameters:
    - active week, defaulting to previous week
    - columns:
      - student name
      - average rubric criterion scores
      - public comments
      - average total grade
    - no pagination
    - format: `HTML`
  - Student-facing visibility restrictions:
    - private comments are never shown
    - evaluators are never shown
  - Report generation algorithm:
    - average each rubric criterion across teammate evaluations
    - overall grade uses the `UC-31` algorithm
- Related use cases:
  - `UC-31`

## UC-30: Set Up an Instructor Account

- Primary actor: `Instructor`
- Trigger: The instructor clicks the registration link in the invitation email.
- Description: The instructor creates an account to supervise senior design projects.
- Preconditions:
  - `PRE-1`: An invitation email has been sent.
- Postconditions:
  - `POST-1`: The instructor account is set up.
- Main success scenario:
  1. The instructor clicks the registration link.
  2. The system opens the registration page and asks for account details.
  3. The instructor enters details and finishes.
  4. The system validates the input.
  5. The system displays details for confirmation.
  6. The instructor confirms or returns to editing.
  7. The system saves the account.
  8. The system redirects to the login page.
- Extensions:
  - `2a`: Account already exists.
  - `4a`: Input validation rule violation.
- Priority: `High`
- Frequency of use: `Approximately 2 users, average of 1 usage per year`
- Associated information:
  - Details:
    - first name
    - middle initial
    - last name
    - password
    - reenter password, which must match
  - The instructor may cancel before submission.

## UC-31: Generate a Peer Evaluation Report Of the Entire Senior Design Section

- Primary actor: `Instructor`
- Trigger: The instructor indicates she wants a section-wide peer evaluation report.
- Description: The instructor generates a report to understand student performance within team settings.
- Preconditions:
  - `PRE-1`: The instructor is logged into the system.
- Postconditions:
  - `POST-1`: The report details are returned and displayed.
- Main success scenario:
  1. The instructor starts report generation.
  2. The system asks for report parameters.
  3. The instructor enters the required parameters.
  4. The system validates them.
  5. The system generates and displays the report.
  6. The system delivers the report in the specified format.
- Extensions:
  - `4a`: Input validation rule violation.
  - `5a`: No data is returned.
- Priority: `High`
- Frequency of use: `Approximately 2 users, average of 1 usage per week`
- Associated information:
  - Report parameters:
    - active week, defaulting to previous week
    - columns:
      - student name
      - grade
      - comments
    - sorting by last name ascending
    - no pagination
    - format: `HTML`
  - The report shows who did not turn in a peer evaluation for that week.
  - Report generation algorithm:
    - obtain all peer evaluations received by a student for that week
    - compute a total score for each evaluation by summing rubric criteria
    - compute the average of those total scores
  - Detailed drill-down may show:
    - evaluator
    - criterion scores
    - public comments
    - private comments

## UC-32: Generate a WAR Report Of a Senior Design Team

- Primary actor: `Instructor`, `Student`
- Trigger: The user indicates she wants a WAR report for a team.
- Description: The user reviews team contribution details for a given week.
- Preconditions:
  - `PRE-1`: The user is logged into the system.
- Postconditions:
  - `POST-1`: The report details are returned and displayed.
- Main success scenario:
  1. The user starts WAR report generation.
  2. The system asks for report parameters.
  3. The user enters parameters.
  4. The system validates them.
  5. The system generates and displays the report.
  6. The system delivers the report in the specified format.
- Extensions:
  - `4a`: Input validation rule violation.
  - `5a`: No data is returned.
- Priority: `High`
- Frequency of use: `Approximately 37 users, average of 1 usage per week`
- Associated information:
  - Report parameters:
    - active week, defaulting to previous week
    - columns:
      - student name
      - activity category
      - planned activity
      - description
      - planned hours
      - actual hours
      - status
    - sort by last name ascending
    - no pagination
    - format: `HTML`
  - The report shows who did not submit a WAR that week.
  - Report generation algorithm: `N/A`

## UC-33: Generate a Peer Evaluation Report Of a Student

- Primary actor: `Instructor`
- Trigger: The instructor indicates she wants a peer evaluation report for a specific student.
- Description: The instructor reviews a student's performance over a time period.
- Preconditions:
  - `PRE-1`: The instructor is logged into the system.
- Postconditions:
  - `POST-1`: The report details are returned and displayed.
- Main success scenario:
  1. The instructor starts the report flow.
  2. She views the student through `UC-16`.
  3. She chooses to generate a peer evaluation report.
  4. The system asks for report parameters.
  5. The instructor enters parameters.
  6. The system validates them.
  7. The system generates and displays the report.
  8. The system delivers the report in the specified format.
- Extensions:
  - `6a`: Input validation rule violation.
  - `7a`: No data is returned.
- Priority: `High`
- Frequency of use: `2 users, average of 10 usages per week`
- Associated information:
  - Report parameters:
    - period: start active week to end active week
    - columns:
      - week
      - grade
      - comments
    - sort by week in chronological order
    - no pagination
    - format: `HTML`
  - Report algorithm:
    - same grade algorithm as `UC-31`
  - Detailed drill-down:
    - same peer-evaluation detail model as `UC-31`

## UC-34: Generate a WAR Report Of the Student

- Primary actor: `Instructor`
- Trigger: The instructor indicates she wants a WAR report for a specific student.
- Description: The instructor reviews how a student contributed over a period of time.
- Preconditions:
  - `PRE-1`: The instructor is logged into the system.
- Postconditions:
  - `POST-1`: The report details are returned and displayed.
- Main success scenario:
  1. The instructor starts the report flow.
  2. She views the student through `UC-16`.
  3. She chooses to generate a WAR report.
  4. The system asks for report parameters.
  5. The instructor enters parameters.
  6. The system validates them.
  7. The system generates and displays the report.
  8. The system delivers the report in the specified format.
- Extensions:
  - `6a`: Input validation rule violation.
  - `7a`: No data is returned.
- Priority: `High`
- Frequency of use: `2 users, average of 10 usages per week`
- Associated information:
  - Report parameters:
    - period: start active week to end active week
    - columns:
      - activity category
      - planned activity
      - description
      - planned hours
      - actual hours
      - status
    - sort by active week in chronological order
    - no pagination
    - format: `HTML`
  - Report generation algorithm: `N/A`

## Business Rules

- `BR-1`: Every senior design team must be assigned at least one instructor. It is common for a team to have two instructors: one TCU instructor and one client. An instructor may be assigned to multiple teams.
- `BR-2`: For fall, active weeks are usually weeks 5-15. Winter holidays are inactive weeks. For spring, active weeks are weeks 1-15. Students are only allowed to submit peer evaluations during active weeks, but they can submit weekly activities outside active weeks.
- `BR-3`: Peer evaluation cannot be edited once completed. The source marks this with `TODO`, which conflicts with `UC-28`.
- `BR-4`: A student can only submit a peer evaluation for the previous week. A student has one week to complete it. Missed peer evaluations cannot be made up.
- `BR-5`: For student-facing peer-evaluation reports, a student can only see rubric criterion scores, public comments, and the overall grade.
