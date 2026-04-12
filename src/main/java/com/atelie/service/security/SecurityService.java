package com.atelie.service.security;

import com.atelie.db.user.Role;
import com.atelie.db.user.User;
import com.atelie.service.user.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityService {
    private final UserService userService;

    public SecurityService(UserService userService) {
        this.userService = userService;
    }

    private static final String LOGOUT_SUCCESS_URL = "/login";

    public UserDetails getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return (UserDetails) context.getAuthentication().getPrincipal();
        }
        // Anonymous or no authentication.
        return null;
    }

    public Role getUserRole() {
        UserDetails authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser == null) {
            return Role.ANONYMOUS;
        }
        String username = authenticatedUser.getUsername();
        Optional<User> user = userService.findUser(username);
        if (user.isPresent()) {
            return user.get().getRole();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

    public void logout() {
        UI.getCurrent()
                .getPage()
                .setLocation(LOGOUT_SUCCESS_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(
                VaadinServletRequest.getCurrent().getHttpServletRequest(),
                null,
                null
        );
    }
}