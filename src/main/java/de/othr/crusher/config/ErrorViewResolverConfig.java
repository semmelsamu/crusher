package de.othr.crusher.config;

import java.util.Map;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * Routes Spring Boot’s default error handling to the Thymeleaf template under
 * {@code templates/pages/error.html}.
 * <p>
 * This keeps the existing {@link org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController}
 * behavior (status codes and error attributes) while aligning the view location
 * with the app’s page directory structure.
 * </p>
 */
@Configuration
public class ErrorViewResolverConfig {

    /**
     * Resolves all error views to {@code pages/error}, preserving the provided status and model.
     *
     * @return an ErrorViewResolver pointing to the pages directory
     */
    @Bean
    ErrorViewResolver customErrorViewResolver() {
        return (request, status, model) -> viewForPagesDirectory(status, model);
    }

    private ModelAndView viewForPagesDirectory(HttpStatus status, Map<String, Object> model) {
        return new ModelAndView("pages/error", model, status);
    }
}
