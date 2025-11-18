package de.othr.crusher.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration class.
 * <p>
 * Configures authentication and authorization for the application. Enables method-level
 * security with {@link EnableMethodSecurity} and defines password encoding and HTTP security rules.
 * </p>
 * <p>
 * Provides a {@link PasswordEncoder} bean for encoding passwords with BCrypt and a
 * {@link SecurityFilterChain} that:
 * <ul>
 *     <li>Restricts access to the H2 console to users with the ADMIN role</li>
 *     <li>Allows public access to static resources (CSS, JS, images, webjars)</li>
 *     <li>Allows public access to the login page</li>
 *     <li>Requires authentication for all other requests</li>
 *     <li>Enables default form login and logout</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Bean for password encoding using BCrypt.
     *
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the HTTP security filter chain.
     *
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/h2-console/**").hasRole("ADMIN")
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());
        return http.build();
    }
}