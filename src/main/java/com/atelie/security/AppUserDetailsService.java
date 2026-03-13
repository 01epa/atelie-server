package com.atelie.security;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService extends InMemoryUserDetailsManager {
    private UserDetails user = User.builder().username("user") // password user
            .password("{bcrypt}$2a$10$9Ib4CHv9MXaqtpd75JkQoOckbRmFqlR.LJZo0GIjKPAYs6TxxWofm").roles("USER").build();

    private UserDetails admin = User.builder().username("admin") // password admin
            .password("{bcrypt}$2a$10$nkyAjGFOeQ8NWr1pd1ooR.DPBdpikX.cUK6WE00eUlh/cdwOiu6hK").roles("USER", "ADMIN")
            .build();

    public AppUserDetailsService() {
        createUser(user);
        createUser(admin);
    }
}
