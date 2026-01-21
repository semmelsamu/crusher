<p align="center"><a href="https://webflow-pwa.com/" target="_blank"><img src="src/main/resources/static/images/logo.png" alt="Webflow PWA Logo" /></a></p>

# Boulder / rock climbing tracking and management tool

## Running

We recommend running the application using Docker.

Build the image:

```zsh
docker build -t crusher .
```

Run the container (persists uploads on the host):

```zsh
docker run --rm -p 8080:8080 -v "$(pwd)/uploads:/app/uploads" crusher
```

Once started, the application will run under http://localhost:8080.

## Development

Prerequisites:

- Java 21 (project toolchain requires Java 21).
- The Gradle wrapper is included (`./gradlew`) - you don't need a global Gradle installation.

Setup:

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

Once started, the application will run under http://localhost:8080.

### Optional: run with production profile

```zsh
SPRING_PROFILES_ACTIVE=prod ./gradlew bootRun
```

## Test accounts

Seeded from `src/main/resources/data.sql`, password for all accounts is `test`.

- alice / test (USER, ROLE_USER)
- bob / test (SETTER, ROLE_SETTER)
- klaus / test (OWNER, ROLE_OWNER)
- crusher / test (ADMIN, ROLE_ADMIN)

## H2 Console

http://localhost:8080/h2-console (restricted to ADMIN role). JDBC URL: `jdbc:h2:mem:app`, user `sa`, password `password`.

## Further documentation

All detailed documentation can be found in `docs`:

- Repository Structure
- API documentation
- Wireframes
- Class Diagrams
- Product backlog
- Best Practices
