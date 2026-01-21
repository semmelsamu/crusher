# Project structure / general architecture

## Overview

Crusher is a Spring Boot 3.5 application with a server-rendered UI (Thymeleaf) and a small REST API.
The runtime uses an in-memory H2 database by default and stores uploads on disk.
The frontend uses Tailwind CSS compiled via PostCSS and served as static assets.

## Top-level layout

- docs/ - Project documentation, diagrams, and wireframes.
- src/ - Java sources and application resources.
- crusher/ - Bruno API collection for manual testing.
- uploads/ - Local file uploads (sector images) mounted in Docker.
- build/ - Gradle build output.
- node_modules/ - Frontend dependencies.
- build.gradle, settings.gradle - Gradle build configuration.
- Dockerfile - Multi-stage container build.
- package.json, postcss.config.js - Frontend build tooling.
- gradlew, gradlew.bat - Gradle wrapper.

## Java packages (src/main/java/de/othr/crusher)

- CrusherApplication - Spring Boot entry point.
- config/ - SecurityConfig (auth, roles, API error handling) and WebConfig (uploads, HTTP method filter).
- controller/ - MVC controllers for HTML pages and REST controllers under /api.
- dto/ - View and API DTOs (statistics, auth, user, go, etc.).
- model/ - JPA entities and enums.
- repository/ - Spring Data repositories and query helpers.
- service/ - Business logic and integrations (statistics, email, weather, crowd level, image storage).
- utils/ - Login helpers and startup password encoding.

## Resources (src/main/resources)

- templates/ - Thymeleaf layouts, components, and pages (see docs/views.md).
- static/ - CSS, JS, images, manifest, service worker.
- application.properties - Default configuration.
- application-dev.properties - Dev overrides (live reload, local config import).
- data.sql - Seed data for the H2 database.
- messages.properties - Text resources for the UI.

## Frontend build pipeline

- Tailwind is compiled via PostCSS into static/css/\_build.css.
- Gradle tasks:
    - npmInstall: installs frontend dependencies.
    - buildTailwind: builds CSS once.
    - watchTailwind: watches CSS during development.
    - dev: runs Spring Boot with the dev profile and starts the CSS watcher.
- processResources depends on buildTailwind, so production builds include compiled CSS.

## Runtime storage and assets

- uploads/ stores sector images under uploads/sectors/{sectorId}/... and is exposed at /uploads/\*\*.
- static/manifest.json and static/sw.js enable the PWA shell and asset caching.

## API tooling

- crusher/ contains a Bruno collection with pre-built requests for Auth, Users, Goes, and Statistics.

## Tests

- src/test/java contains the Spring Boot test suite.
- Run ./gradlew test.
