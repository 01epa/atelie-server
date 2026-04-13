package com.atelie.ui.order;

import com.atelie.db.order.Order;
import com.atelie.db.order.OrderStatus;
import com.atelie.db.user.Role;
import com.atelie.service.order.OrderService;
import com.atelie.service.security.SecurityService;
import com.atelie.ui.AbstractView;
import com.atelie.ui.MainLayout;
import com.atelie.ui.Notifications;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
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
    private final SecurityService securityService;
    private final Grid<Order> grid = new Grid<>(Order.class, false);
    private final Checkbox showOnlyMine = new Checkbox(t("orders.showOnlyMine"));
    private final Checkbox showOnlyActive = new Checkbox(t("orders.showOnlyActive"));
    private ComboBox<OrderStatus> statusFilter;
    private IntegerField numberFilter;
    private final boolean isOwner;
    private Grid.Column<Order> statusColumn;

    public OrdersListView(OrderService orderService,
                          SecurityService securityService) {
        this.orderService = orderService;
        this.securityService = securityService;
        isOwner = securityService.getUserRole() == Role.OWNER;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        Button add = new Button(t("orders.create"),
                e -> UI.getCurrent().navigate(ORDERS + "/" + NEW_ORDER));
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        showOnlyActive.setValue(true);

        showOnlyMine.addValueChangeListener(e -> refresh());
        showOnlyActive.addValueChangeListener(e -> {
            updateStatusColumnHeader();
            refresh();
        });

        VerticalLayout leftColumn = new VerticalLayout(
                add
        );
        leftColumn.setPadding(false);
        leftColumn.setSpacing(true);
        leftColumn.setWidthFull();
        leftColumn.setAlignItems(Alignment.START);
        leftColumn.setVisible(isOwner);

        VerticalLayout rightColumn = new VerticalLayout(
                showOnlyMine,
                showOnlyActive
        );
        rightColumn.setPadding(false);
        rightColumn.setSpacing(true);
        rightColumn.setWidthFull();
        rightColumn.setAlignItems(Alignment.START);

        HorizontalLayout topBar = new HorizontalLayout(
                leftColumn,
                rightColumn
        );
        topBar.setWidthFull();
        topBar.setAlignItems(Alignment.START);
        topBar.setFlexGrow(1, leftColumn, rightColumn);

        add(topBar);

        configureGrid();

        addAndExpand(grid);

        refresh();
    }

    private void configureGrid() {
        grid.setSizeFull();

        numberFilter = new IntegerField();
        numberFilter.setPlaceholder(t("orders.column.number"));
        numberFilter.setClearButtonVisible(true);
        numberFilter.setWidthFull();
        numberFilter.addValueChangeListener(e -> refresh());

        grid.addColumn(Order::getOrderNumber)
                .setHeader(numberFilter)
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

        statusColumn = grid.addColumn(o -> t(o.getStatus()))
                .setHeader(t("orders.column.status"))
                .setSortable(true)
                .setKey("status");

        updateStatusColumnHeader();

        grid.addComponentColumn(order -> {
                    Button doneBtn = new Button(t("orders.button.done"));
                    doneBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                    doneBtn.getStyle().set("min-width", "80px");
                    doneBtn.getStyle().set("width", "80px");
                    doneBtn.getStyle().set("padding", "0 5px");
                    doneBtn.getStyle().set("background-color", "#a8e6a3");
                    doneBtn.getStyle().set("color", "black");
                    doneBtn.setVisible(order.getStatus() != OrderStatus.DONE && order.getStatus() != OrderStatus.CANCELLED && isOwner);

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

            String username = securityService.getAuthenticatedUser().getUsername();
            return orderService.list(
                    PageRequest.of(
                            query.getPage(),
                            query.getPageSize(),
                            sort
                    ),
                    username,
                    showOnlyMine.getValue(),
                    showOnlyActive.getValue(),
                    numberFilter.getValue(),
                    statusFilter == null ? OrderStatus.ACCEPTED : statusFilter.getValue()
            ).stream();
        });
    }

    private void updateStatusColumnHeader() {
        if (showOnlyActive.getValue()) {
            statusFilter = null;
            statusColumn.setHeader(t("orders.column.status"));
        } else {
            statusColumn.setHeader(createStatusFilter());
        }
    }

    private Component createStatusFilter() {
        statusFilter = new ComboBox<>();
        statusFilter.setItems(OrderStatus.values());
        statusFilter.setItemLabelGenerator(this::t);
        statusFilter.setPlaceholder(t("orders.column.status"));
        statusFilter.setClearButtonVisible(true);
        statusFilter.setWidthFull();
        statusFilter.addValueChangeListener(e -> refresh());
        return statusFilter;
    }

    @Override
    public String getPageTitle() {
        return t("orders.title");
    }
}