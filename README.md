# Crusher — Quickstart

## Prerequisites

- Java 21 (project toolchain requires Java 21).
- The Gradle wrapper is included (`./gradlew`) — you don't need a global Gradle installation.

## Quickstart (terminal)

1. Verify your Java version (should be Java 21):

```zsh
java -version
```

3. Build the project (downloads dependencies, compiles, runs tests):

```zsh
./gradlew clean build
```

4. Start the app for development (hot reload via Spring DevTools):

```zsh
./gradlew bootRun
```

5. Access & test data

- Open the app at: http://localhost:8080 — you will be redirected to the login page if not authenticated.
- Test accounts (seeded from `src/main/resources/data.sql`, password for all accounts is `test` before encoding by the startup runner):
	- alice / test (ROLE_USER)
	- bob / test (ROLE_SETTER)
	- klaus / test (ROLE_OWNER)
	- crusher / test (ROLE_ADMIN)

- H2 Console: http://localhost:8080/h2-console (restricted to ADMIN role). JDBC URL: `jdbc:h2:mem:app`, user `sa`, password `password`.
