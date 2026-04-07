package com.atelie.security.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;

@Route("logout")
@AnonymousAllowed
public class LogoutView extends Main {
    public LogoutView(AuthenticationContext authenticationContext) {
        add(new Button("Logout", event -> authenticationContext.logout()));
    }
}