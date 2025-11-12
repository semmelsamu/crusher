package edu.oth.crusher.utils.login;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PasswordEncoderRunner implements CommandLineRunner {

    private final JdbcTemplate jdbc;
    private final PasswordEncoder encoder;

    public PasswordEncoderRunner(JdbcTemplate jdbc, PasswordEncoder encoder) {
        this.jdbc = jdbc;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        var users = jdbc.query("SELECT id, password FROM users",
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
