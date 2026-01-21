package de.othr.crusher.utils.login;

import java.util.Map;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Component that encodes user passwords in the database on application startup.
 *
 * <p>Implements {@link CommandLineRunner} and runs once when the Spring Boot application starts. It
 * retrieves all users from the database and encodes any plaintext passwords using the provided
 * {@link PasswordEncoder}.
 *
 * <p>This ensures that passwords stored in the database are always securely encoded, even if
 * inserted in plaintext via a SQL script.
 */
@Component
public class PasswordEncoderRunner implements CommandLineRunner {

  private final JdbcTemplate jdbc;
  private final PasswordEncoder encoder;

  /**
   * Constructor that injects the JdbcTemplate and PasswordEncoder.
   *
   * @param jdbc used to access and update the user table
   * @param encoder used to encode plaintext passwords
   */
  public PasswordEncoderRunner(JdbcTemplate jdbc, PasswordEncoder encoder) {
    this.jdbc = jdbc;
    this.encoder = encoder;
  }

  /**
   * Encodes all plaintext passwords in the database.
   *
   * <p>Queries all users and checks if the password is already encoded. If not, it encodes the
   * password using {@link PasswordEncoder} and updates the database.
   *
   * @param args command-line arguments (ignored)
   */
  @Override
  public void run(String... args) {
    var users =
        jdbc.query(
            "SELECT id, password FROM users",
            (rs, n) -> Map.of("id", rs.getLong("id"), "pw", rs.getString("password")));

    for (var u : users) {
      String pw = (String) u.get("pw");
      if (!pw.startsWith("$2a$")) {
        String encoded = encoder.encode(pw);
        jdbc.update("UPDATE users SET password=? WHERE id=?", encoded, u.get("id"));
      }
    }
  }
}
