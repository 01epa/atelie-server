package com.atelie.base.ui;

import com.atelie.order.ui.OrdersListView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import static com.atelie.Role.ROLE_ADMIN;
import static com.atelie.Role.ROLE_USER;

@Route("")
@PageTitle("Главная")
@RolesAllowed({ROLE_ADMIN, ROLE_USER})
class MainView extends VerticalLayout {
    MainView() {
        UI.getCurrent().navigate(OrdersListView.ORDERS);
    }
}
