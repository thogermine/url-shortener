package dk.lundogbendsen.springbootcourse.urlshortener.controller.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityIntercepter());
    }

    @Bean
    public SecurityIntercepter securityIntercepter() {
        return new SecurityIntercepter();
    }
}
