# Database Structure & Initialization

This document describes how the in-memory database is organized and initialized in the Crusher application.

## Technology

H2 is an in-memory database.
Spring Data JPA is used for Object-Relational Mapping.

Dependencies:
- implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
- runtimeOnly 'com.h2database:h2'

## Database Lifecycle

The H2 database is recreated on every application startup.
Spring generates all tables based on your JPA entities.
`data.sql` is used to insert initial records.

## Folder Structure
```
src/
├── main/
├── resources/
├── application.properties
└── data.sql
```

## Initial Data

`data.sql` runs automatically at startup and inserts initial data into our database.

## Usage

Spring Data JPA automatically creates a database table for a @Entity with a JpaRepository.

##### Repository Example
```
public interface UserRepository extends JpaRepository<User, Long> {}
```
##### Entity Example
```
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue
    private Long id;

    private String name;
    private String role;
    private String password;
}
```
## H2 Console

The `/h2-console` endpoint provides a `web-based interface` to view and manage the in-memory H2 database used in the Crusher application.
Accessible at /h2-console during development.

Only Users with Role 'ADMIN' can access /h2-console.
The following specifications in `SecurityConfig` are necessary to use it, they must be deleted when in production:
- .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
- .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))

