package com.atelie.order.ui;

import com.atelie.order.db.Order;
import com.atelie.order.service.OrderService;
import com.atelie.security.SecurityService;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;

import static com.atelie.order.ui.OrdersListView.NEW_ORDER;
import static com.atelie.order.ui.OrdersListView.ORDERS;

@Route(value = ORDERS + "/:orderId")
@PageTitle("Заказ")
@AnonymousAllowed
public class OrderEditView extends VerticalLayout implements BeforeEnterObserver {

    private final OrderService orderService;
    private final SecurityService securityService;

    public OrderEditView(OrderService orderService,
                         SecurityService securityService) {
        this.orderService = orderService;
        this.securityService = securityService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String orderId = event.getRouteParameters().get("orderId").orElse(null);
        Order order;

        if (NEW_ORDER.equals(orderId)) {
            order = new Order();
        } else {
            try {
                Long id = Long.parseLong(orderId);
                order = orderService.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Заказ с id=" + id + " не найден"));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Неверный формат orderId: " + orderId);
            }
        }

        add(new OrderForm(
                orderService,
                order,
                List.of("user", "admin"),
                securityService.getAuthenticatedUser() != null
        ));
    }
}