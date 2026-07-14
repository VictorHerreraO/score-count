---
name: create-pr
description: Create a pull request with proper structure and documentation
---

# Create Pull Request

When the user asks you to create a pull request, follow this process:

## 1. Analyze Current Changes

Review the current branch to understand all changes since diverging from main:
```bash
git log origin/main..HEAD --oneline
git diff origin/main...HEAD
```

## 2. Determine PR Type

Classify as either:
- **FIX**: Bug fixes, typos, chores, refactoring
- **FEATURE**: New functionality, enhancements

## 3. Structure the PR

Use the appropriate template below. Fill every section with clear, concise content.

### Fix PR Template

```markdown
## Fix: [Brief description]

**Related Issue:** Closes #[issue-number]

### Summary
- Problem: [What was broken/incorrect]
- Solution: [What was changed]
- Impact: [What this fixes]

### Key Changes
- `file/path.kt:123` - [Key change]
- `another/file.kt:456` - [Key change]

### Testing
Steps to verify:
1. [Reproduction step or test command]
2. [Expected result]

### Notes
[Any challenges, trade-offs, or important context]
```

### Feature PR Template

```markdown
## Feature: [Brief description]

**Related Issue:** Implements #[issue-number]

### Motivation
[Why this feature? What problem does it solve?]

### Implementation
- **Architecture**: [Layer(s) affected: Domain/Data/UI]
- **Key components**: [New classes/files created]
- **Integration points**: [How it connects to existing code]

### Changes
- `domain/model/Foo.kt` - [New domain model]
- `ui/foo/FooScreen.kt` - [New UI screen]
- [List other significant files]

### Testing
```bash
./gradlew test --tests "com.example.FooTest"
```

### Notes
[Any challenges, future improvements, or important context]
```

## 4. Create the PR

Push changes and create PR using the GitHub MCP tool `mcp__github__create_pull_request`:
```
Use the mcp__github__create_pull_request tool with:
- owner: Repository owner
- repo: Repository name
- title: "[Fix/Feature]: Title"
- head: Current branch name
- base: "main"
- body: [Paste filled template here]

🤖 Generated with [Your Name]
```

## Important Notes

- Use file paths with line numbers (e.g., `path/to/file.kt:123`) when referencing code
- Keep descriptions concise but complete
- Always include test results or verification steps
- Sign off with the Claude Code attribution
