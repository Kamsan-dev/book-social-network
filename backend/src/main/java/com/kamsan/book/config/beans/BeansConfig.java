package com.kamsan.book.config.beans;

import com.kamsan.book.config.security.CorsProps;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@RequiredArgsConstructor
public class BeansConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfiguration corsConfiguration(CorsProps props) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(props.getAllowedOrigins());
        config.setAllowedMethods(props.getAllowedMethods());
        config.setAllowedHeaders(props.getAllowedHeaders());
        config.setExposedHeaders(props.getExposedHeaders());
        config.setAllowCredentials(props.getAllowCredentials());
        config.setMaxAge(props.getMaxAge());
        return config;
    }


}
