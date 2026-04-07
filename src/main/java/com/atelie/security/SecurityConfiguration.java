package com.atelie.security;

import com.atelie.security.ui.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static com.atelie.order.ui.OrdersListView.ORDERS;
import static com.vaadin.flow.spring.security.VaadinSecurityConfigurer.vaadin;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain vaadinSecurityFilterChain(HttpSecurity http) {
        return http
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers("/images/*.png",
                                        "/line-awesome/**",
                                        "/*.css",
                                        "/aura/**").permitAll()
                                .requestMatchers("/" + ORDERS + "/*").permitAll()
                )
                .with(vaadin(), configurer -> configurer.loginView(LoginView.class))
                .build();
    }
}