package com.atelie.ui.order;

import com.atelie.service.order.OrderService;
import com.atelie.service.security.SecurityService;
import com.atelie.service.user.UserService;
import com.atelie.ui.MainLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import static com.atelie.ui.order.OrdersListView.ORDERS;

@Route(value = ORDERS + "/:orderId", layout = MainLayout.class)
@PageTitle("Заказ")
@PermitAll
public class OrderEditView extends OrderView {
    public OrderEditView(OrderService orderService,
                         UserService userService,
                         SecurityService securityService) {
        super(orderService,
                userService,
                securityService);
    }
}