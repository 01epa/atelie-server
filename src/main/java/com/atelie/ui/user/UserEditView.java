package com.atelie.ui.user;

import com.atelie.db.user.Role;
import com.atelie.db.user.User;
import com.atelie.service.security.SecurityService;
import com.atelie.service.user.UserService;
import com.atelie.ui.AbstractView;
import com.atelie.ui.MainLayout;
import com.atelie.ui.Notifications;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static com.atelie.ui.user.UsersListView.NEW_USER;
import static com.atelie.ui.user.UsersListView.USERS;

@Route(value = USERS + "/:userId", layout = MainLayout.class)
@PageTitle("Пользователь")
@RolesAllowed(Role.ROLE_ADMIN)
public class UserEditView extends AbstractView implements BeforeEnterObserver {

    private static final Logger log = LoggerFactory.getLogger(UserEditView.class);
    private final UserService userService;
    protected final SecurityService securityService;

    public UserEditView(UserService userService,
                        SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        removeAll();
        String userId = event.getRouteParameters().get("userId").orElse(null);
        User user;

        if (userId == null || NEW_USER.equals(userId)) {
            user = new User();
        } else {
            try {
                user = userService.findUser(UUID.fromString(userId))
                        .orElseThrow();
            } catch (Exception e) {
                log.error("Can't open user form", e);
                handleError(event, t("user.notFound", userId));
                return;
            }
        }
        add(new UserForm(
                userService,
                user
        ));
    }

    protected void handleError(BeforeEnterEvent event, String message) {
        boolean loggedIn = securityService.getAuthenticatedUser() != null;

        if (loggedIn) {
            event.forwardTo(UsersListView.class);

            UI.getCurrent().access(() -> Notifications.error(message));
        } else {
            removeAll();
            add(
                    new H3(t("error")),
                    new Span(message)
            );
        }
    }
}