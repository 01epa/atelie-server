package com.atelie.ui.user;

import com.atelie.db.user.Role;
import com.atelie.db.user.User;
import com.atelie.service.user.UserService;
import com.atelie.ui.AbstractView;
import com.atelie.ui.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static com.atelie.service.user.UserService.DEFAULT_SORTING;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.by;

@Route(value = UsersListView.USERS, layout = MainLayout.class)
@RolesAllowed(Role.ROLE_ADMIN)
public class UsersListView extends AbstractView implements HasDynamicTitle {

    public static final String USERS = "users";
    public static final String NEW_USER = "new";

    private final UserService userService;
    private final Grid<User> grid = new Grid<>(User.class, false);

    public UsersListView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        Button add = new Button(t("users.create"),
                e -> UI.getCurrent().navigate(USERS + "/" + NEW_USER));
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

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

        grid.addColumn(User::getUsername)
                .setHeader(t("users.column.username"))
                .setAutoWidth(true)
                .setSortable(true)
                .setKey("username");

        grid.addColumn(User::getFirstName)
                .setHeader(t("users.column.firstname"))
                .setSortable(true)
                .setKey("first_name");

        grid.addColumn(User::getLastName)
                .setHeader(t("users.column.lastname"))
                .setSortable(true)
                .setKey("last_name");

        grid.addColumn(o -> t(o.getStatus()))
                .setHeader(t("users.column.status"))
                .setSortable(true)
                .setKey("status");

        grid.addColumn(o -> t(o.getRole()))
                .setHeader(t("users.column.role"))
                .setSortable(true)
                .setKey("role");

        grid.addItemDoubleClickListener(e ->
                UI.getCurrent().navigate(USERS + "/" + e.getItem().getId())
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

            return userService.list(
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
        return t("users.title");
    }
}