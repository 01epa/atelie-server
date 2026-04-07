package com.atelie.order.ui;

import com.atelie.base.ui.AbstractView;
import com.atelie.base.ui.MainLayout;
import com.atelie.order.OrderStatus;
import com.atelie.order.db.Order;
import com.atelie.order.service.OrderService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.domain.PageRequest;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Route(value = OrdersListView.ORDERS, layout = MainLayout.class)
@PermitAll
public class OrdersListView extends AbstractView implements HasDynamicTitle {

    public static final String ORDERS = "orders";
    public static final String NEW_ORDER = "new";

    private final OrderService orderService;
    private final Grid<Order> grid = new Grid<>(Order.class, false);

    public OrdersListView(OrderService orderService) {
        this.orderService = orderService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        Button add = new Button(t("orders.create"),
                e -> UI.getCurrent().navigate(ORDERS + "/" + NEW_ORDER));

        HorizontalLayout topBar = new HorizontalLayout(add);
        topBar.setWidthFull();
        topBar.setPadding(true);
        add(topBar);

        configureGrid();

        addAndExpand(grid);

        refresh();
    }

    private void configureGrid() {
        grid.setSizeFull();

        grid.addColumn(Order::getOrderNumber)
                .setHeader(t("orders.column.number"))
                .setAutoWidth(true);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm")
                .withZone(ZoneId.systemDefault());

        grid.addColumn(o -> o.getDueDate() != null ? formatter.format(o.getDueDate()) : "-")
                .setHeader(t("orders.column.dueDate"));

        grid.addColumn(Order::getPrice)
                .setHeader(t("orders.column.price"));

        grid.addColumn(Order::getComment)
                .setHeader(t("orders.column.comment"))
                .setAutoWidth(true);

        grid.addColumn(o -> t(o.getStatus()))
                .setHeader(t("orders.column.status"));

        grid.addComponentColumn(order -> {
                    Button doneBtn = new Button(t("orders.button.done"));
                    doneBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                    doneBtn.getStyle().set("min-width", "80px");
                    doneBtn.getStyle().set("width", "80px");
                    doneBtn.getStyle().set("padding", "0 5px");
                    doneBtn.getStyle().set("background-color", "#a8e6a3");
                    doneBtn.getStyle().set("color", "black");

                    doneBtn.setVisible(order.getStatus() != OrderStatus.DONE && order.getStatus() != OrderStatus.CANCELLED);

                    doneBtn.addClickListener(e -> {
                        order.setStatus(OrderStatus.DONE);
                        orderService.save(order);
                        refresh();
                    });

                    return doneBtn;
                })
                .setFlexGrow(0)
                .setAutoWidth(true);

        grid.addItemDoubleClickListener(e ->
                UI.getCurrent().navigate(ORDERS + "/" + e.getItem().getId())
        );
    }

    private void refresh() {
        grid.setItems(query ->
                orderService.list(
                        PageRequest.of(
                                query.getPage(),
                                query.getPageSize()
                        )
                ).stream()
        );
    }

    @Override
    public String getPageTitle() {
        return t("orders.title");
    }
}