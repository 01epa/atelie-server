package com.atelie.order.ui;

import com.atelie.Notifications;
import com.atelie.base.ui.AbstractView;
import com.atelie.order.db.Order;
import com.atelie.order.service.OrderService;
import com.atelie.security.SecurityService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.atelie.order.ui.OrdersListView.NEW_ORDER;

@Route(value = OrderView.ORDER + "/:orderId")
@PageTitle("Заказ")
@AnonymousAllowed
public class OrderView extends AbstractView implements BeforeEnterObserver {

    private static final Logger log = LoggerFactory.getLogger(OrderView.class);
    public static final String ORDER = "order";
    protected final OrderService orderService;
    protected final SecurityService securityService;

    public OrderView(OrderService orderService,
                     SecurityService securityService) {
        this.orderService = orderService;
        this.securityService = securityService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        removeAll();
        String orderId = event.getRouteParameters().get(ORDER + "Id").orElse(null);
        Order order;

        if (NEW_ORDER.equals(orderId)) {
            order = new Order();
        } else {
            try {
                int orderNumber = Integer.parseInt(orderId);
                order = orderService.findByOrderNumber(orderNumber)
                        .orElseThrow();
            } catch (Exception e) {
                log.error("Can't open " + ORDER + " form", e);
                handleError(event, t(ORDER + ".notFound", orderId));
                return;
            }
        }
        boolean editable = securityService.getAuthenticatedUser() != null;
        add(new OrderForm(
                orderService,
                order,
                List.of("user", "admin"),
                editable
        ));
    }

    protected void handleError(BeforeEnterEvent event, String message) {
        boolean loggedIn = securityService.getAuthenticatedUser() != null;

        if (loggedIn) {
            event.forwardTo(OrdersListView.class);

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