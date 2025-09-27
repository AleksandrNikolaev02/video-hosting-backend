package dev.alex.auth.starter.auth_spring_boot_starter.config;

import dev.alex.auth.starter.auth_spring_boot_starter.aspect.AuthorizeAspect;
import dev.alex.auth.starter.auth_spring_boot_starter.filter.RoleFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthorizeConfig {
    @Bean
    public AuthorizeAspect authorizeAspect() {
        return new AuthorizeAspect();
    }

    @Bean
    public RoleFilter roleFilter() {
        return new RoleFilter();
    }
}
