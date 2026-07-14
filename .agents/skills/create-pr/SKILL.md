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

- [Feature template](./references/feature-template.md)
- [Fix template](./references/fix-template.md)

## 4. Create the PR

Push changes and create PR using the `gh` cli tool.

## Important Notes

- Use file paths with line numbers (e.g., `path/to/file.kt:123`) when referencing code
- Keep descriptions concise but complete
- Always include test results or verification steps
- Sign off with the attribution
