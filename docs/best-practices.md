## Git conventions

### Branching conventions

The `main` branch is protected. Any feature or change should be added via a pull request.

The convention is inspired by [Conventional Branch](https://conventional-branch.github.io/)

There are 3 types of branches:

- `feat/feature-name` for new features
- `fix/fix-name` for bugfixes
- `doc/doc-name` for documentation updates

### Commit conventions

We use the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) standard for commit messages.

Commits should look like this:

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

The commit types should match the branch types.

## Coding conventions

### Code style

- We use the Astyle formatter for java files
- We use prettier to format CSS, HTML and JavaScript
