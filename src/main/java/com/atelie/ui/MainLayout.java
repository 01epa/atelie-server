package com.atelie.ui;

import com.atelie.db.user.Role;
import com.atelie.db.user.User;
import com.atelie.service.security.SecurityService;
import com.atelie.service.user.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static com.atelie.ui.order.OrdersListView.ORDERS;
import static com.atelie.ui.user.UserForm.changePasswordDialog;
import static com.atelie.ui.user.UsersListView.USERS;

@PermitAll
public final class MainLayout extends AppLayout {

    private final SecurityService securityService;
    private final UserService userService;

    MainLayout(SecurityService securityService,
               UserService userService) {
        this.securityService = securityService;
        this.userService = userService;
        setPrimarySection(Section.DRAWER);

        addToDrawer(createHeader(), new Scroller(createSideNav()));
        HorizontalLayout navbar = new HorizontalLayout();
        navbar.setWidthFull();
        navbar.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        navbar.add(createUserMenu());
        addToNavbar(navbar);
    }

    private Component createHeader() {
        var appLogo = VaadinIcon.CUBES.create();
        appLogo.setSize("48px");
        appLogo.setColor("green");
        var appName = new Span("Ателье");
        appName.getStyle().setFontWeight(Style.FontWeight.BOLD);
        var header = new VerticalLayout(appLogo, appName);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        return header;
    }

    private SideNav createSideNav() {
        var nav = new SideNav();
        nav.addClassNames(LumoUtility.Margin.Horizontal.MEDIUM);
        nav.addItem(new SideNavItem("Список заказов", "/" + ORDERS));
        if (securityService.getUserRole() == Role.ADMIN) {
            nav.addItem(new SideNavItem("Список пользователей", "/" + USERS));
        }
        return nav;
    }

    private Component createUserMenu() {
        String username = securityService.getAuthenticatedUser().getUsername();
        User user = userService.findUser(username).orElseThrow(() -> new UsernameNotFoundException(username));
        HorizontalLayout userLayout = new HorizontalLayout(
                VaadinIcon.USER.create(),
                new Span(user.getFullName())
        );
        userLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        userLayout.setSpacing(true);
        MenuBar menuBar = new MenuBar();
        MenuItem userItem = menuBar.addItem(userLayout);
        userItem.getSubMenu().addItem("Сменить пароль", e -> changePasswordDialog(this, userService, user));
        userItem.getSubMenu().addItem("Выйти", e -> securityService.logout());
        return menuBar;
    }
}
