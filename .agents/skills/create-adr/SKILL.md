---
name: create-adr
description: Creatr ADR
metadata:
  reference: https://github.com/architecture-decision-record/architecture-decision-record
---

## File name conventions for ADRs

Examples:

- 001-choose-database.md
- 002-format-timestamps.md
- 003-manage-passwords.md
- 004-handle-exceptions.md

Our file name convention:

- [mention number convention]
- The name has a present tense imperative verb phrase. This helps readability and matches our commit message format.
- The name uses lowercase and dashes (same as this repo). This is a balance of readability and system usability.
- The extension is markdown. This can be useful for easy formatting.

## Suggestions for writing good ADRs

### Characteristics of a good ADR:

- Rationale: Explain the reasons for doing the particular AD. This can include the context (see below), pros and cons of various potential choices, feature comparisons, cost/benefit discussions, and more.
- Specific: Each ADR should be about one AD, not multiple ADs.
- Timestamps: Identify when each item in the ADR is written. This is especially important for aspects that may change over time, such as costs, schedules, scaling, and the like.
- Immutable: Don't alter existing information in an ADR. Instead, amend the ADR by adding new information, or supersede the ADR by creating a new ADR.

### Characteristics of a good "Context" section in an ADR:

- Explain your organization's situation and business priorities.
- Include rationale and considerations based on social and skills makeups of your teams.
- Include pros and cons that are relevant, and describe them in terms that align with your needs and goals.

### Characteristics of good "Consequences" section in an ADR:

- Explain what follows from making the decision. This can include the effects, outcomes, outputs, follow ups, and more.
- Include information about any subsequent ADRs. It's relatively common for one ADR to trigger the need for more ADRs, such as when one ADR makes a big overarching choice, which in turn creates needs for more smaller decisions.
- Include any after-action review processes. It's typical for teams to review each ADR one month later, to compare the ADR information with what's happened in actual practice, in order to learn and grow.

### A new ADR may take the place of a previous ADR:

- When an AD is made that replaces or invalidates a previous ADR, then a new ADR should be created

## Common questions for ADRs

### What justifies raising an ADR?

Consider areas such as your organization's team ways of working, your software system structure, cross-team coordination, long-term maintainability, external interfaces, who you want to benefit, and the like.

Example answer: We want to create an ADR when we want future developers to understand the “why” of what we're doing.

### What justifies not raising an ADR?

Consider areas such as decisions that are not about architecture, or are tiny such as minimal-risk or self-contained or single-developer, or are already fully covered elsewhere such as by standards or policies or documentation, or are temporary such as workarounds or proofs of concepts or orexperiments.

Example answer: We want to skip an ADR when a decision is limited in scope and time and risk and cost, or is already covered elsewhere.

### What is the lifecycle of an ADR?

Consider areas such as the creation process, research process, decisioning process, implementation process, and sunsetting process. Consider how to track the ADR lifecycle over time, such as how to move the ADR from one state to the next state, and also how to communicate this to stakeholders.

Example answer: We want an ADR to have five lifecycle stages: Initiating → Researching → Evaluating → Implementing → Maintaining → Sunsetting.

### What are criteria for lifecycle steps of an ADR?

Consider areas such as acceptance criteria for an ADR, meaning how do you know it's good enough to progress from one lifecycle step to the next? Is the problem clearly articulated? Have the alternatives been considered? Are trade-offs well-enough understood and documented? Is all relevant context in place? Are all relevant stakeholders involved? Has all feedback been incorporated?

Example answer: We want an ADR to be voted on by stakeholders when the active team has 1) completed their research, 2) completed their evaluation, 3) published the ADR proposal to the stakeholders with a request for comments and a timebox of one week, 4) all stakeholder comments have been incorporated and addressed.

## Architecture diagrams & views & viewpoints

An architecture diagram is called an "architecture view".

An "architecture view" is an instance of a "architecture viewpoint".

An "architecture viewpoint" has a specific audience with specific concerns in mind.

### Architecture viewpoint examples, view examples, and diagram examples:

- A Use Case Diagram shows use cases to management/customers, which precedes requirements, which precedes the software - architecture.
- A Deployment Diagram shows the physical hardware/computers that the software components are deployed to.
- A Data Flow Diagram shows how data moves through the system and is transformed.
- A Sequence Diagram is used to show how protocols like HTTP work on a time axis.
- An Activity Diagram depicts the workflow of activities a software system undertakes, like an NPC AI.

## References

- [ADR Template](./references/template.md)
