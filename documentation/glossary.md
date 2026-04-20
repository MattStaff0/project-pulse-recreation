# Glossary

## Purpose

This document defines terms used throughout the planning, design, and software development of Project Pulse. It is intended for current and future software engineers as well as stakeholders who need a shared vocabulary for the project domain.

The glossary gives all parties a consistent definition of domain terms, phrases, and abbreviations, and it clarifies concepts that may otherwise be ambiguous across design and development artifacts.

## Scope

This glossary is associated with the Project Pulse peer evaluation tool. The terms here are intended to support the vision and scope document, use case document, and related project materials.

## Overview

The remainder of this document lists domain terms together with their definitions. Terms that are closely related may share a numbering family in the original source artifacts, but they are presented here in a straightforward markdown structure for easier reference.

## Definitions

### Senior Design Section

Texas Christian University's Department of Computer Science offers a two-course senior sequence:

- `COSC 40943: Software Engineering`
- `COSC 40993: Senior Design`

In this sequence, senior students work in teams to deliver real software solutions for external clients. Teams handle the full lifecycle: problem definition, analysis, design, implementation, testing, deployment, and documentation. Students are expected to practice agile development and standard software engineering practices such as code review, refactoring, source control, CI/CD, and automated testing.

Within Project Pulse, a `Senior Design Section` denotes a particular academic-year offering of this sequence. The section begins near the end of August and typically ends near the end of April of the following year.

Section naming format:

- `YYYY-YYYY`
- Examples: `2023-2024`, `2024-2025`

### Senior Design Team

A `Senior Design Team` is a group of senior-level students collaborating on a capstone project that integrates their academic knowledge and skills. The team works through the full project lifecycle, from concept and design to implementation, testing, presentation, and delivery.

This experience emphasizes:

- teamwork
- project management
- technical execution
- communication

A team usually consists of `5-6 students`.

### TCU Online

`TCU Online` is the university course management system used by TCU. Students can upload homework and view grades there. It does not support peer evaluations directly, which is one of the key reasons Project Pulse exists.

### Academic Year

An `Academic Year` is the period of time used by educational institutions to organize instruction and evaluation. It typically spans early fall of one year to early summer of the next year and is divided into semesters or similar terms.

This period provides the framework for:

- curriculum planning
- course scheduling
- examinations
- holidays
- graduation

### Active Week

Once a senior design project begins, students must submit weekly activity reports and peer evaluations. An `Active Week` is a week during which those submissions are required.

Because the section includes a winter holiday break, the system must allow an admin to mark certain weeks as inactive so students are not required to submit during those weeks.

Week definition:

- A week starts on `Monday`
- A week ends on `Sunday`
- Monday's date may be used as the week identifier

### Weekly Activity Report (WAR)

A `Weekly Activity Report`, or `WAR`, provides a structured overview of a student's weekly project work. It helps communicate progress, blockers, and next steps to teammates and instructors.

A WAR contains multiple activities. For each activity, a student records:

- activity category
- planned activity
- activity description
- planned hours
- actual hours
- status

The original source artifact references an example WAR spreadsheet:

- <https://docs.google.com/spreadsheets/d/1jpxBQ8Gvv94bRl1gSpBzXRFxJu7IQXKjQZ5n28-EpD8/edit?usp=sharing>

### Peer Evaluation

A `Peer Evaluation` allows team members to assess each other's contributions, performance, and teamwork every week. The evaluation is intended to:

- highlight strengths
- identify areas for improvement
- improve accountability within the team

### Public Comments

When evaluating a teammate, a student may optionally provide `Public Comments`. These comments are sent to the teammate being evaluated.

### Private Comments

When evaluating a teammate, a student may optionally provide `Private Comments`. These comments are visible to the instructor only and are not shared with the teammate being evaluated.

### Rubric

A `Rubric` is the scoring guide used to evaluate peer performance. It defines the evaluation criteria and the score range for each criterion.

In the provided planning artifact, the active rubric asks students to rate teammates from `1-10` for the following:

- quality of work
- productivity
- proactivity
- respect for others
- response to criticism
- meeting performance

### Criterion

Within a rubric, a `Criterion` is a single measurable aspect of performance, work quality, behavior, or contribution. Together, the criteria define the expectations for a student's performance in a given week.

Examples of criteria include:

- clarity of work
- depth of contribution
- teamwork
- participation
- technical skill

In Project Pulse, criteria are defined as part of the peer evaluation rubric and are used to compute both detailed feedback and overall peer-evaluation grades.
