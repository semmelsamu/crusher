package de.othr.crusher.utils.login;

import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service for loading user details for Spring Security.
 * <p>
 * Implements the {@link UserDetailsService} interface and retrieves user information
 * from the database using the {@link UserRepository}.
 * </p>
 * <p>
 * This service is used by Spring Security to authenticate users based on their username.
 * It fetches the user entity from the database, converts it into a {@link UserDetails}
 * instance, and provides Spring Security with the user’s credentials and roles.
 * </p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their username from the database.
     *
     * @param username the username to look up
     * @return the {@link UserDetails} for authentication
     * @throws UsernameNotFoundException if no user with the given username exists
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return User.withUsername(user.getName())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}
