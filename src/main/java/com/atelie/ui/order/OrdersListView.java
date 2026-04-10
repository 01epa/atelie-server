package com.atelie.ui.order;

import com.atelie.ui.Notifications;
import com.atelie.ui.AbstractView;
import com.atelie.ui.MainLayout;
import com.atelie.db.order.OrderStatus;
import com.atelie.db.order.Order;
import com.atelie.service.order.OrderService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static com.atelie.service.order.OrderService.DEFAULT_SORTING;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.by;

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
                .setAutoWidth(true)
                .setSortable(true)
                .setKey("orderNumber");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm")
                .withZone(ZoneId.systemDefault());

        grid.addColumn(o -> o.getDueDate() != null ? formatter.format(o.getDueDate()) : "-")
                .setHeader(t("orders.column.dueDate"))
                .setSortable(true)
                .setKey("dueDate");

        grid.addColumn(Order::getPrice)
                .setHeader(t("orders.column.price"))
                .setSortable(true)
                .setKey("price");

        grid.addColumn(Order::getComment)
                .setHeader(t("orders.column.comment"))
                .setAutoWidth(true)
                .setSortable(true)
                .setKey("comment");

        grid.addColumn(o -> t(o.getStatus()))
                .setHeader(t("orders.column.status"))
                .setSortable(true)
                .setKey("status");

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
                        Notifications.success(t("order.ready", order.getOrderNumber()));
                        refresh();
                    });

                    return doneBtn;
                })
                .setFlexGrow(0)
                .setAutoWidth(true);

        grid.addItemDoubleClickListener(e ->
                UI.getCurrent().navigate(ORDERS + "/" + e.getItem().getOrderNumber())
        );
    }

    private void refresh() {
        grid.setItems(query -> {
            var sortOrders = query.getSortOrders();

            var sort = sortOrders.isEmpty()
                    ? DEFAULT_SORTING
                    : by(
                    sortOrders.stream()
                            .map(order -> new Sort.Order(
                                    order.getDirection() == SortDirection.ASCENDING
                                            ? ASC
                                            : DESC,
                                    order.getSorted()
                            ))
                            .toList()
            );

            return orderService.list(
                    PageRequest.of(
                            query.getPage(),
                            query.getPageSize(),
                            sort
                    )
            ).stream();
        });
    }

    @Override
    public String getPageTitle() {
        return t("orders.title");
    }
}