package edu.oth.crusher.utils.login;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final JdbcTemplate jdbcTemplate;

    public CustomUserDetailsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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