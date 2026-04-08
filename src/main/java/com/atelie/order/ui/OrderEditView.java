package com.atelie.order.ui;

import com.atelie.base.ui.MainLayout;
import com.atelie.order.service.OrderService;
import com.atelie.security.SecurityService;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import static com.atelie.order.ui.OrdersListView.ORDERS;

@Route(value = ORDERS + "/:orderId", layout = MainLayout.class)
@PageTitle("Заказ")
@PermitAll
public class OrderEditView extends OrderView {
    public OrderEditView(OrderService orderService,
                         SecurityService securityService) {
        super(orderService, securityService);
    }
}