package com.atelie.base.ui;

import com.atelie.security.SecurityService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

@Layout
@AnonymousAllowed
public final class MainLayout extends AppLayout {

    MainLayout(@Autowired SecurityService securityService) {
        setPrimarySection(Section.DRAWER);
        Button logout = new Button("Выйти", click ->
                securityService.logout());
        logout.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout logoutWrapper = new HorizontalLayout(logout);
        logoutWrapper.setPadding(true);
        logoutWrapper.setWidthFull();
        logoutWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        addToDrawer(createHeader(), new Scroller(createSideNav()), logoutWrapper);
    }

    private Component createHeader() {
        // TODO Replace with real application logo and name
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
        nav.addItem(new SideNavItem("Список заказов", "/orders"));
        return nav;
    }
}
