# Crusher — Quickstart

## Prerequisites

- Java 21 (project toolchain requires Java 21).
- The Gradle wrapper is included (`./gradlew`) — you don't need a global Gradle installation.

## Quickstart (terminal)

1. Verify your Java version (should be Java 21):

```zsh
java -version
```

2. Build the project (downloads dependencies, compiles, runs tests):

```zsh
./gradlew clean build
```

3. Start the app for development (Spring DevTools + Tailwind watcher, uses the `dev` profile):

```zsh
./gradlew dev
```

4. Access & test data

- Open the app at: http://localhost:8080 — you will be redirected to the login page if not authenticated.
- Test accounts (seeded from `src/main/resources/data.sql`, password for all accounts is `test` before encoding by the startup runner):
    - alice / test (ROLE_USER)
    - bob / test (ROLE_SETTER)
    - klaus / test (ROLE_OWNER)
    - crusher / test (ROLE_ADMIN)

- H2 Console: http://localhost:8080/h2-console (restricted to ADMIN role). JDBC URL: `jdbc:h2:mem:app`, user `sa`, password `password`.

### Optional: run with production profile

```zsh
SPRING_PROFILES_ACTIVE=prod ./gradlew bootRun
```

## Docker

Build the image:

```zsh
docker build -t crusher .
```

Run the container (persists uploads on the host):

```zsh
docker run --rm -p 8080:8080 -v "$(pwd)/uploads:/app/uploads" crusher
```
