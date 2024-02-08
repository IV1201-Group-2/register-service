package com.example.registerservice.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfiguration configures mappings to allow Cross-origin requests.
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    /**
     * Method allows Cross-origin requests, in order for any HTTP
     * method and any origin to be able to make requests.
     * {@code @Autowired} provides automatic dependency injection.
     *
     * @param registration , registers CORS mappings.
     */
    @Override
    public void addCorsMappings(CorsRegistry registration) {
        registration.addMapping("/**").allowedMethods("*");
    }
}
