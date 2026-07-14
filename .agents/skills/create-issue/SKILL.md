---
name: create-issue
description: Create a development task (issue) for a new feature, bug fix, or research task.
---

# Create Issue

When the user asks you to create a task, an issue, a ticket, or a new feature request, follow this process:

## 1. Understand and Refine the Request

For the current conversation context:

- **Analyze**: Start by analyzing the user's request to identify the core problem to be solved or the feature to be added.
- **Groom**: Refine the request with the user until there is a clear and unambiguous understanding of the task. This may involve several back-and-forth questions to groom the issue.
- **Goal**: The goal is to have a well-defined task that is actionable by a developer. Do not create issues for vague tasks. If the request is ambiguous, ask clarifying questions before proceeding.

## 2. Determine Issue Type

Classify the issue into one of the following types:
- **FEATURE**: A new feature or functionality enhancement.
- **BUGFIX**: A bug fix, typo, or correction.
- **RESEARCH**: An investigation or exploration task.

## 3. Structure the Issue

Use the appropriate template below based on the issue type. Fill every section with clear, concise, and actionable content.

- [Bugfix template](./references/bugfix-template.md)
- [Feature template](./references/feature-template.md)
- [Research template](./references/research-template.md)

## 4. Create the Issue

Use the `gh` cli tool to create the issue in the repository. Use a descriptive title with the appropriate prefix (`Feature`, `Bugfix`, `Research`) and paste the filled template into the body.

## Important Notes

- The title should be descriptive and concise.
- The description should provide enough context for a developer to understand the task.
- Acceptance criteria should be specific, measurable, and testable.
