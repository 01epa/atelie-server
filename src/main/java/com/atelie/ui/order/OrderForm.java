package com.atelie.ui.order;

import com.atelie.db.order.Order;
import com.atelie.db.order.OrderStatus;
import com.atelie.db.order.PaymentMethod;
import com.atelie.db.order.PaymentStatus;
import com.atelie.db.user.Role;
import com.atelie.db.user.User;
import com.atelie.service.order.OrderService;
import com.atelie.service.security.SecurityService;
import com.atelie.service.user.UserService;
import com.atelie.ui.AbstractView;
import com.atelie.ui.Notifications;
import com.vaadin.flow.component.ModalityMode;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static com.atelie.ui.order.OrdersListView.ORDERS;

public class OrderForm extends AbstractView {
    private final Binder<Order> binder = new Binder<>(Order.class);

    public OrderForm(OrderService orderService,
                     UserService userService,
                     SecurityService securityService,
                     Order order) {
        Role userRole = securityService.getUserRole();
        boolean editable = userRole == Role.OWNER;

        setWidthFull();
        // ---------- поля ----------
        IntegerField orderNumber = new IntegerField(t("order.number"));
        orderNumber.setWidthFull();

        TextField comment = new TextField(t("order.comment"));
        comment.setWidthFull();

        List<User> users = userService.findAllUsers();

        ComboBox<User> acceptedBy = new ComboBox<>(t("order.acceptedBy"));
        acceptedBy.setItems(users);
        acceptedBy.setWidthFull();
        acceptedBy.setItemLabelGenerator(User::getFullName);

        ComboBox<User> assignedTo = new ComboBox<>(t("order.assignedTo"));
        assignedTo.setItems(users);
        assignedTo.setWidthFull();
        assignedTo.setItemLabelGenerator(User::getFullName);

        acceptedBy.addValueChangeListener(e -> {
            if (e.getValue() != null && assignedTo.getValue() == null) {
                assignedTo.setValue(e.getValue());
            }
        });
        acceptedBy.setWidthFull();

        DateTimePicker dueDate = new DateTimePicker(t("order.dueDate"));
        dueDate.setMin(LocalDateTime.now());
        dueDate.setStep(Duration.ofHours(1));
        dueDate.setWidthFull();

        IntegerField price = new IntegerField(t("order.price"));
        price.setMin(0);
        price.setWidthFull();

        ComboBox<PaymentStatus> paymentStatus = new ComboBox<>(t("order.payment.status"));
        paymentStatus.setItems(PaymentStatus.values());
        paymentStatus.setItemLabelGenerator(this::t);
        paymentStatus.setWidthFull();

        IntegerField partiallyPaid = new IntegerField(t("order.payment.partiallyPaid"));
        partiallyPaid.setMin(0);
        partiallyPaid.setWidthFull();

        ComboBox<PaymentMethod> paymentMethod = new ComboBox<>(t("order.payment.method"));
        paymentMethod.setItems(PaymentMethod.values());
        paymentMethod.setItemLabelGenerator(this::t);
        paymentMethod.setWidthFull();

        ComboBox<OrderStatus> status = new ComboBox<>(t("order.status"));
        status.setItems(OrderStatus.values());
        status.setItemLabelGenerator(this::t);
        status.setWidthFull();

        // ---------- логика UI ----------
        paymentStatus.addValueChangeListener(e -> {
            boolean partially = e.getValue() == PaymentStatus.PARTIALLY;
            partiallyPaid.setVisible(partially);
            if (!partially) {
                partiallyPaid.setValue(0);
            }
            binder.validate();
        });
        paymentStatus.setWidthFull();

        // ---------- binder ----------
        binder.forField(orderNumber)
                .asRequired(t("validation.orderRequired"))
                .withValidator(
                        v -> v != null && v > 0,
                        t("validation.orderNumber"))
                .bind(Order::getOrderNumber, editable ? Order::setOrderNumber : null);
        binder.forField(comment)
                .bind(Order::getComment, editable ? Order::setComment : null);
        binder.forField(dueDate)
                .asRequired(t("validation.dueDate"))
                .withValidator(
                        dt -> dt.getHour() >= 9 && dt.getHour() <= 21,
                        t("validation.timeRange"))
                .bind(
                        o -> o.getDueDate() != null
                                ? LocalDateTime.ofInstant(
                                o.getDueDate(),
                                ZoneId.systemDefault())
                                : null,
                        editable
                                ? (o, v) -> o.setDueDate(
                                v != null
                                        ? v.atZone(ZoneId.systemDefault()).toInstant()
                                        : null)
                                : null
                );
        binder.forField(price)
                .asRequired(t("validation.priceRequired"))
                .withValidator(
                        v -> v != null && v > 0,
                        t("validation.price"))
                .bind(Order::getPrice, editable ? Order::setPrice : null);
        binder.forField(paymentStatus)
                .bind(Order::getPaymentStatus, editable ? Order::setPaymentStatus : null);
        binder.forField(partiallyPaid)
                .withValidator(v -> {
                    if (paymentStatus.getValue() == PaymentStatus.PARTIALLY) {
                        return v != null && v > 0;
                    }
                    return true;
                }, t("validation.partial"))
                .bind(Order::getPartiallyPaid, editable ? Order::setPartiallyPaid : null);
        binder.forField(paymentMethod)
                .bind(Order::getPaymentMethod, editable ? Order::setPaymentMethod : null);
        binder.forField(status)
                .bind(Order::getStatus, editable ? Order::setStatus : null);
        binder.forField(acceptedBy)
                .bind(Order::getAcceptedBy, editable ? Order::setAcceptedBy : null);
        binder.forField(assignedTo)
                .bind(Order::getAssignedTo, editable ? Order::setAssignedTo : null);
        binder.readBean(order);

        // ---------- layout ----------
        VerticalLayout orderLeft = new VerticalLayout(orderNumber, price);
        VerticalLayout orderRight = new VerticalLayout(dueDate, comment);

        HorizontalLayout orderBlock = new HorizontalLayout(orderLeft, orderRight);
        orderBlock.setWidthFull();
        orderBlock.setFlexGrow(1, orderLeft, orderRight);

        VerticalLayout execLeft = new VerticalLayout(acceptedBy, status);
        VerticalLayout execRight = new VerticalLayout(assignedTo);

        HorizontalLayout executionBlock = new HorizontalLayout(execLeft, execRight);
        executionBlock.setWidthFull();
        executionBlock.setFlexGrow(1, execLeft, execRight);

        VerticalLayout payLeft = new VerticalLayout(paymentStatus, partiallyPaid);
        VerticalLayout payRight = new VerticalLayout(paymentMethod);

        HorizontalLayout paymentBlock = new HorizontalLayout(payLeft, payRight);
        paymentBlock.setWidthFull();
        paymentBlock.setFlexGrow(1, payLeft, payRight);

        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setWidthFull();

        if (userRole != Role.ANONYMOUS) {
            H3 orderTitle = new H3(t("order.order.title"));
            H3 executionTitle = new H3(t("order.execution.title"));
            H3 paymentTitle = new H3(t("order.payment.title"));

            formLayout.add(orderTitle,
                    orderBlock,
                    executionTitle,
                    executionBlock,
                    paymentTitle,
                    paymentBlock);
            // ---------- кнопки ----------
            Button saveBtn = new Button(t("button.save"), event -> {
                if (!binder.validate().isOk()) {
                    return;
                }
                binder.writeBeanIfValid(order);
                int number = order.getOrderNumber();
                orderService.findActiveByOrderNumber(order.getId(), number).ifPresentOrElse(existingOrder -> {
                    Dialog dialog = new Dialog();
                    dialog.add(new Text(t("order.duplicateOrder", number)));

                    Button goToExisting = new Button(t("order.gotoOrder"), e -> {
                        dialog.close();
                        UI.getCurrent().navigate(ORDERS + "/" + existingOrder.getOrderNumber());
                    });

                    Button closeExisting = new Button(t("order.saveAndCloseOrder"), e -> {
                        existingOrder.setStatus(OrderStatus.DONE);
                        orderService.save(existingOrder);
                        dialog.close();
                        Notifications.success(t("order.ready", number));
                        saveOrder(orderService, order);
                    });

                    Button cancel = new Button(t("button.cancel"), e -> dialog.close());

                    HorizontalLayout actions = new HorizontalLayout(goToExisting, closeExisting, cancel);
                    actions.setSpacing(true);
                    actions.setPadding(true);
                    dialog.add(actions);
                    dialog.setModality(ModalityMode.STRICT);
                    dialog.setCloseOnOutsideClick(true);
                    dialog.open();
                }, () -> saveOrder(orderService, order));
            });
            saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            Button cancelBtn = new Button(t("button.cancel"),
                    e -> UI.getCurrent().navigate(ORDERS));
            cancelBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

            Button deleteBtn = new Button(t("button.delete"), e -> {

                Dialog dialog = new Dialog();

                H3 label = new H3(t("order.deletion", order.getOrderNumber()));
                Button save = new Button(t("button.save"), ev -> {
                    orderService.delete(order);
                    Notifications.warning(t("order.deleted", order.getOrderNumber()));
                    UI.getCurrent().navigate(ORDERS);
                    dialog.close();
                });
                save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                Button cancel = new Button(t("button.cancel"), ev -> dialog.close());

                VerticalLayout layout = new VerticalLayout(label);
                HorizontalLayout buttonLayout = new HorizontalLayout(save, cancel);
                buttonLayout.setWidthFull();
                buttonLayout.setFlexGrow(1, save, cancel);
                layout.add(buttonLayout);
                dialog.add(layout);
                dialog.open();
            });
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
            HorizontalLayout buttons = new HorizontalLayout();
            if (editable) {
                buttons.add(saveBtn);
            }
            buttons.add(cancelBtn);
            if (editable && order.getId() != null) {
                buttons.add(deleteBtn);
            }
            formLayout.add(buttons);
        } else {
            formLayout.add(new H3(t("order.order.title")));
            formLayout.add(orderNumber, dueDate, price, status);
        }
        add(formLayout);
    }

    private void saveOrder(OrderService orderService, Order order) {
        orderService.save(order);
        Notifications.info(t("order.saved", order.getOrderNumber()));
        UI.getCurrent().navigate(ORDERS);
    }
}