# Project Pulse Documentation

This directory contains product-context documents intended to help engineers and AI agents understand what Project Pulse is for, who it serves, and how the system is expected to behave.

These markdown files were derived from planning artifacts and use-case specifications. They should be treated as source-of-intent documents: some details describe the target product vision and may be broader than the current implementation in the codebase.

## Document Map

- [glossary.md](./glossary.md): domain vocabulary used across the project.
- [vision-and-scope.md](./vision-and-scope.md): problem statement, goals, stakeholders, scope, risks, and deployment context.
- [use-cases.md](./use-cases.md): actor-driven functional behavior, use case list, detailed flows, and business rules.

## Project At A Glance

Project Pulse is a web application for Texas Christian University senior design courses. It centralizes two workflows that were previously handled through Google Sheets, Excel files, and TCU Online:

- Weekly Activity Reports (WARs)
- Weekly peer evaluations

The system serves three primary roles:

- `Admin`: manages sections, teams, users, rubrics, and active weeks.
- `Instructor`: supervises teams and generates reports.
- `Student`: logs weekly work, submits peer evaluations, and views personal feedback.

## Core Domain Concepts

- A `Senior Design Section` represents an academic-year offering of the TCU senior design sequence.
- A `Senior Design Team` belongs to a section and usually contains 5-6 students.
- `Active Weeks` determine when peer evaluations are required.
- A `WAR` captures each student's weekly activities, hours, and status.
- A `Peer Evaluation` captures rubric-based teammate feedback plus public and private comments.

## Important Product Boundaries

- The system is intended to reduce manual administrative work, especially around peer evaluation collection and reporting.
- TCU Online / LMS integration is explicitly out of scope in the provided planning artifacts; instructors still upload grades manually.
- The product is designed primarily for the TCU senior design course domain.

## Known Source-Document Inconsistencies

The source material contains a few inconsistencies that future work should reconcile:

- Some detailed use-case headings in the source do not match the numbering in the use-case list.
- `UC-28` says peer evaluations can be edited after submission, while `BR-3` says they cannot be edited once completed.
- Some sections contain `TODO` or placeholder content from the original planning templates.

When implementation decisions depend on one of these conflicts, check the codebase and stakeholder intent before assuming the planning artifact is authoritative.
