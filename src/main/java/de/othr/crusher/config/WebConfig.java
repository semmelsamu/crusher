package de.othr.crusher.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.HiddenHttpMethodFilter;

/**
 * Web configuration class providing additional servlet filters.
 */
@Component
public class WebConfig {

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

