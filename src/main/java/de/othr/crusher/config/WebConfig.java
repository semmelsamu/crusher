package de.othr.crusher.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class providing additional servlet filters.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String uploadBaseDir;

    public WebConfig(@Value("${app.uploads.base-dir:uploads}") String uploadBaseDir) {
        this.uploadBaseDir = uploadBaseDir;
    }

    /**
     * Exposes uploaded assets under /uploads/** by mapping the configured upload
     * directory as a static resource location.
     *
     * @param registry Spring resource handler registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadsPath = Paths.get(uploadBaseDir).toAbsolutePath().normalize();
        String uploadLocation = uploadsPath.toUri().toString();
        if (!uploadLocation.endsWith("/")) {
            uploadLocation = uploadLocation + "/";
        }

        registry.addResourceHandler("/uploads/**").addResourceLocations(uploadLocation);
    }

    /**
     * Registers a HiddenHttpMethodFilter, enabling support for HTTP methods like
     * PUT and DELETE via the _method request parameter.
     *
     * @return registration bean for the filter
     */
    @Bean
    public FilterRegistrationBean<HiddenHttpMethodFilter> hiddenHttpMethodFilter() {
        FilterRegistrationBean<HiddenHttpMethodFilter> filterRegistrationBean =
                new FilterRegistrationBean<>(new HiddenHttpMethodFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
}
