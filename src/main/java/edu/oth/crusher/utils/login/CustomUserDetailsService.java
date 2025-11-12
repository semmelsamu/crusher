package edu.oth.crusher.utils.login;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service for loading user details for Spring Security.
 * <p>
 * Implements the {@link UserDetailsService} interface and retrieves user information
 * from the database using {@link JdbcTemplate}.
 * </p>
 * <p>
 * This class is used by Spring Security to authenticate users by their username.
 * The returned {@link UserDetails} contain the username, password (already encoded),
 * and assigned role.
 * </p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructor that injects the JdbcTemplate.
     *
     * @param jdbcTemplate used to access the user table
     */
    public CustomUserDetailsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Loads a user by their username from the database.
     *
     * @param username the username to look up
     * @return the {@link UserDetails} of the user
     * @throws UsernameNotFoundException if no user with the given username exists
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT * FROM users WHERE name = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) throw new UsernameNotFoundException(username);
            String name = rs.getString("name");
            String password = rs.getString("password");
            String role = rs.getString("role");
            return User.withUsername(name)
                    .password(password)
                    .roles(role)
                    .build();
        }, username);
    }
}