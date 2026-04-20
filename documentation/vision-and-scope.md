# Vision And Scope

## Introduction

This document defines the overarching goals, purpose, and boundaries of Project Pulse. It provides a shared understanding among stakeholders of what the software is intended to achieve and the environment in which it will operate.

It identifies:

- the business problem the software is meant to solve
- the target future-state workflow
- the scope and boundaries of the solution

Detailed fulfillment of these needs is described in the use cases and supporting requirements artifacts.

## Background

Texas Christian University's Department of Computer Science offers a senior design course sequence in which student teams work with real clients on meaningful software projects. Students are responsible for the complete project lifecycle, including:

- problem definition
- analysis
- design
- implementation
- testing
- deployment
- documentation

Within a senior design team, contribution levels vary. Some students are highly active while others contribute less, and communication issues can arise. To manage those dynamics, the course currently uses a student performance tracking process centered on weekly activity reporting and peer evaluation.

Project Pulse exists to improve that process.

## Current Process Flows (As-Is)

### Weekly Activity Reports (WARs)

Current WAR workflow:

- Every student must complete a WAR each week.
- WARs are recorded in shared Google Sheets.
- Students on the same team can view and edit the same spreadsheet.
- Each sheet represents one team's activities for one week.
- The instructor keeps track of the spreadsheet URLs, reviews updates on Tuesday, and enters grades and feedback in TCU Online.

Referenced WAR spreadsheet:

- <https://docs.google.com/spreadsheets/d/1jpxBQ8Gvv94bRl1gSpBzXRFxJu7IQXKjQZ5n28-EpD8/edit?usp=sharing>

Textual WAR flow:

1. On Monday, each student opens the team's shared Google Sheet.
2. The student navigates to the sheet for the correct week.
3. The student records the prior week's activities.
4. On Tuesday, the instructor reviews updated sheets.
5. The instructor grades and leaves feedback through TCU Online.

### Peer Evaluation

Current peer evaluation workflow:

- Students use an Excel spreadsheet to complete peer evaluations for the previous week.
- Each student uploads the file to TCU Online.
- The instructor downloads all submissions, runs a separate Java program to parse and compute results, then uploads grades and comments back to TCU Online.

Textual peer-evaluation flow:

1. Each Tuesday, every student reviews the team's WAR for the previous week.
2. The student completes the peer evaluation form in Excel.
3. The student uploads the spreadsheet to TCU Online.
4. The instructor downloads all submitted forms.
5. The instructor runs a Java program to parse the data.
6. The instructor finalizes grades and compiles comments.
7. The instructor uploads grades and comments to TCU Online.
8. Students later view teammate evaluations through that system.

### Pain Points In The Current Process

The current process improves visibility but remains heavily manual.

WAR pain points:

- students must manually edit the weekly spreadsheet
- instructors must manually review student-entered sheet data
- formatting or entry mistakes may result in incorrect credit

Peer evaluation pain points:

- students must create and upload specially formatted spreadsheets
- instructors must download all files manually
- instructors must run a separate Java tool to compute grades
- instructors must re-upload results manually
- incorrect formatting or missing columns create failure points

Overall, the current process is:

- error-prone
- repetitive
- time-consuming for both students and instructors

## References

The original source notes that references for the WAR and peer evaluation form still need to be completed.

Open reference item:

- `TODO`: formally reference the WAR and peer evaluation form artifacts

## Business Requirements

### Business Opportunity / Problem Statement

The current peer evaluation process in the TCU Computer Science department is burdened by inefficiencies, errors, and delays. Students must download, complete, and upload peer evaluation forms through TCU Online, which creates avoidable friction. Faculty members must manually manage evaluation files and grading, which can delay feedback.

This creates a clear business opportunity:

- automate collection and reporting
- improve accuracy
- reduce instructor effort
- improve turnaround time
- provide a better experience for students and faculty

The expected result is a more efficient and effective peer-evaluation system that supports academic growth and better resource allocation in the department.

### Business Objectives

- `BO-1`: Reduce the instructor's time to grade peer evaluations by `50%`.
- `BO-2`: Increase WAR and peer-evaluation submission rate by `20%`.
- `BO-3`: Reduce students' time to complete WARs and peer evaluations by `25%`.

### Vision Statement

For:

- students of TCU senior design

Who:

- need an easier way to submit and update weekly activity reports and peer evaluations

The product:

- `Project Pulse`

That:

- makes it easier for students to submit weekly activity reports and peer evaluations
- makes it easier for instructors to view and grade them

Unlike:

- the traditional manual process

Our product:

- streamlines the overall workflow
- makes the process more accessible
- reduces pain for both students and instructors

## Proposed New / Improved Process Flows (To-Be)

Project Pulse improves both the WAR workflow and the peer evaluation workflow by bringing student and instructor tasks into a single system.

### Improved WAR Flow

The planning artifact notes that the WAR process conceptually stays the same, but the execution environment changes:

- students and instructors perform WAR-related tasks inside Project Pulse
- manual spreadsheet editing is removed
- the workflow is streamlined and less error-prone

One manual step remains outside scope:

- the instructor still uploads grades to the university LMS

### Improved Peer Evaluation Flow

The peer evaluation process is also simplified:

1. Students review the team's WAR inside Project Pulse.
2. Students complete peer evaluations inside Project Pulse.
3. Project Pulse compiles evaluations for the section.
4. Project Pulse calculates scores and generates feedback.
5. Instructors review aggregated results directly in Project Pulse.
6. Students view their own scores and feedback in Project Pulse.
7. Instructors still manually upload grades and feedback to the LMS.

This target workflow emphasizes:

- reduced manual work
- fewer formatting and file-handling errors
- centralized reporting
- improved visibility for students and instructors

## Business Risks

- `RI-1`: If the system is deployed on a cloud service provider, the Computer Science Department must pay recurring annual cloud fees.
- `RI-2`: The student peer-evaluation database may be breached by attackers.
- `RI-3`: The application could end up being more confusing than the previous process.
- `RI-4`: The application may be too specialized for the TCU senior design course and not broad enough for other use cases.

## Business Assumptions And Dependencies

- `AS-1`: The system should use technologies the client understands and can maintain after delivery.

## Stakeholder Profiles And User Descriptions

### Stakeholder Profiles

| Stakeholder | Major value or benefit | Attitude | Major features of interest | Constraints | End user? |
| --- | --- | --- | --- | --- | --- |
| Students | Easier submission and viewing of weekly activities and peer evaluations | Supportive | Submitting everything in one place without downloading or uploading files | Students must be instructed on how to use the platform | Yes |
| Instructors | Easier compilation and grading of student work; better visibility into team dynamics | Supportive | Viewing WARs and generating peer-evaluation reports every week | Instructors must be instructed on how to use the platform | Yes |

### User Environment

Users will access the application through a web browser on:

- desktop
- laptop
- mobile device

This is intended to work regardless of operating system.

### Alternatives And Competition

The primary alternative is the current manual workflow:

- students upload peer evaluations to TCU Online
- instructors download each one
- instructors calculate team grades manually

The planning artifact treats the current workflow itself as the main alternative to adopting Project Pulse.

## Scope And Limitations

### Product Perspective

Project Pulse is the central platform for the workflow. According to the provided context diagram description:

- instructors manage courses, WAR templates, peer-evaluation templates, and review submissions
- students submit WARs, complete peer evaluations, and view scores and feedback
- Gmail is used to send automated email notifications such as reminders and updates

This places Project Pulse at the center of the domain while relying on email infrastructure for communication.

### Major Features / Scope

- `FE-1`: Manage senior design sections, teams, and students.
- `FE-3`: Submit weekly activity reports and peer evaluations.
- `FE-4`: Generate weekly activity and peer-evaluation grades for the entire senior design section.

The source explicitly states that the use-case document contains more detail than this feature summary.

### Deployment Considerations

The system is expected to be hosted on a cloud platform such as `Microsoft Azure`.
