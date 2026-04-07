package com.atelie.security.ui;

import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Вход")
@Route(value = "login", autoLayout = false)
@AnonymousAllowed
public class LoginView extends Main implements BeforeEnterObserver {

    private final LoginForm login;

    public LoginView() {


        login = new LoginForm();
        login.setI18n(createLoginI18n());
        login.setAction("login");
        login.setForgotPasswordButtonVisible(false);

        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layout.add(login);
        layout.setSizeFull();

        add(layout);
        setSizeFull();
    }

    private static LoginI18n createLoginI18n() {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle("Вход");
        i18n.getForm().setUsername("Имя пользователя");
        i18n.getForm().setPassword("Пароль");
        i18n.getForm().setSubmit("Войти");
        i18n.getForm().setForgotPassword("Забыли пароль?");
        i18n.getErrorMessage().setTitle("Ошибка входа");
        i18n.getErrorMessage().setMessage("Неверное имя пользователя или пароль");
        i18n.getErrorMessage().setUsername("Имя пользователя обязательно");
        i18n.getErrorMessage().setPassword("Пароль обязателен");
        return i18n;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }
}
