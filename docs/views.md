# Thymeleaf Template Structure

This document describes the organization of Thymeleaf templates in the Crusher application.

## Folder Structure

```
views/
├── components/       # Reusable UI components (fragments)
│   ├── header.html   # Navigation header
│   ├── footer.html   # Page footer
│   ├── card.html     # Card component
│   └── ...           # Other reusable components
│
├── layouts/          # Page layouts (base templates)
│   ├── main.html     # Main application layout
│   └── ...           # Other layouts (admin, auth, etc.)
│
└── pages/            # Actual page templates
    ├── stats.html    # Statistics page
    └── ...           # Other pages
```

## Usage Guidelines

### Components (`components/`)

Components are reusable template fragments that can be included in multiple pages. They use Thymeleaf's fragment syntax.

**Example Component (`components/card.html`):**

```html
<div th:fragment="card(title, content)" class="card">
    <h3 th:text="${title}">Title</h3>
    <p th:text="${content}">Content</p>
</div>
```

**Using a Component:**

```html
<div th:replace="~{components/card :: card('My Title', 'My Content')}"></div>
```

### Layouts (`layouts/`)

Layouts define the overall page structure and common elements (header, footer, navigation). Pages extend layouts to maintain consistent structure.

**Example Layout (`layouts/main.html`):**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
    <head>
        <title layout:title-pattern="$CONTENT_TITLE - $LAYOUT_TITLE">App</title>
    </head>
    <body>
        <header th:replace="~{components/header :: header}"></header>
        <main layout:fragment="content">
            <!-- Page content here -->
        </main>
        <footer th:replace="~{components/footer :: footer}"></footer>
    </body>
</html>
```

**Using a Layout:**

```html
<!DOCTYPE html>
<html
    xmlns:th="http://www.thymeleaf.org"
    xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
    layout:decorate="~{layouts/main}"
>
    <head>
        <title>My Page</title>
    </head>
    <body>
        <div layout:fragment="content">
            <h1>Page Content</h1>
        </div>
    </body>
</html>
```

### Pages (`pages/`)

Pages are the actual view templates rendered by controllers. They typically extend a layout and define page-specific content.

**Controller Reference:**

```java
@Controller
public class HomeController {
    @GetMapping("/stats")
    public String home() {
        return "pages/stats";  // Returns pages/stats.html
    }
}
```

## Best Practices

1. **Components**: Keep them small, focused, and reusable
2. **Layouts**: Define common page structure and include global components
3. **Pages**: Focus on page-specific content, extend layouts for structure
4. **Naming**: Use kebab-case for filenames (e.g., `user-profile.html`)
5. **Organization**: Group related pages in subdirectories (e.g., `pages/admin/`, `pages/auth/`)

## Common Thymeleaf Patterns

### Including Fragments

```html
<!-- Replace entire element -->
<div th:replace="~{components/header :: header}"></div>

<!-- Insert content inside element -->
<div th:insert="~{components/header :: header}"></div>
```

### Fragment Parameters

```html
<!-- Component definition -->
<div th:fragment="alert(type, message)">
    <div th:class="'alert alert-' + ${type}" th:text="${message}"></div>
</div>

<!-- Usage -->
<div th:replace="~{components/alert :: alert('success', 'Saved!')}"></div>
```

### Conditional Fragments

```html
<!-- Show only if authenticated -->
<div sec:authorize="isAuthenticated()">
    <span sec:authentication="name">User</span>
</div>
```

## Thymeleaf Layout Dialect

To use the layout features (layout:decorate, layout:fragment), add this dependency to `build.gradle`:

```gradle
implementation 'nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect'
```

This enables advanced layout composition features demonstrated in this structure.
