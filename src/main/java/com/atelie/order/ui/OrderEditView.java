package com.atelie.order.ui;

import com.atelie.base.ui.MainLayout;
import com.atelie.order.db.Order;
import com.atelie.order.service.OrderService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

import static com.atelie.Role.ROLE_ADMIN;
import static com.atelie.Role.ROLE_USER;
import static com.atelie.order.ui.OrdersListView.NEW_ORDER;
import static com.atelie.order.ui.OrdersListView.ORDERS;

@Route(value = ORDERS + "/:orderId", layout = MainLayout.class)
@PageTitle("Заказ")
@RolesAllowed({ROLE_ADMIN, ROLE_USER})
public class OrderEditView extends VerticalLayout implements BeforeEnterObserver {

    private final OrderService orderService;

    public OrderEditView(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        String orderId = event.getRouteParameters().get("orderId").orElse(null);

        Order order;

        if (NEW_ORDER.equals(orderId)) {
            order = new Order();
        } else {
            Long id = Long.parseLong(orderId);
            order = orderService.findById(id).orElseThrow();
        }

        add(new OrderForm(
                orderService,
                order,
                List.of("user", "admin"),
                () -> UI.getCurrent().navigate(ORDERS)
        ));
    }
}