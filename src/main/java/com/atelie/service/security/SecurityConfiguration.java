package com.atelie.service.security;

import com.atelie.ui.order.OrderView;
import com.atelie.ui.login.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static com.vaadin.flow.spring.security.VaadinSecurityConfigurer.vaadin;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain vaadinSecurityFilterChain(HttpSecurity http) {
        return http
                .with(vaadin(), configurer -> configurer.loginView(LoginView.class))
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers("/images/*.png",
                                        "/line-awesome/**",
                                        "/*.css",
                                        "/aura/**").permitAll()
                                .requestMatchers("/" + OrderView.ORDER + "/*").permitAll()
                )
                .build();
    }
}