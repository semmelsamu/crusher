## Project structure / general architecture

### Folder structure

#### `docs/`

This directory contains documentation about the project, including style guides. All files in this directory must follow the kebab-case naming convention.

#### `src/main/java/de/othr/crusher/`

-   `controller/` - Contains Spring REST controllers for handling HTTP requests and responses.
-   `model/` - Domain model classes representing application data structures.
-   `repository/` - Interfaces for database access, usually extending Spring Data JPA.
-   `service/` - Service classes implementing business logic and interacting with repositories.
-   `utils/` - Utility classes and helper functions for general use.

#### `src/main/resources/`

-   `views/` - Server-rendered view templates (e.g., Thymeleaf) for dynamic HTML.
-   `static/` - Public static resources such as CSS, JavaScript, and images.
-   `application.properties` - Spring Boot application configuration properties.
-   `message.properties` - Resource bundle for internationalization and localization.
-   `data.sql` - SQL script for preloading database data at startup.
