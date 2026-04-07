package com.atelie.order.ui;

import com.atelie.base.ui.AbstractView;
import com.atelie.order.OrderStatus;
import com.atelie.order.PaymentMethod;
import com.atelie.order.PaymentStatus;
import com.atelie.order.db.Order;
import com.atelie.order.service.OrderService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class OrderForm extends AbstractView {
    private final Binder<Order> binder = new Binder<>(Order.class);

    public OrderForm(OrderService orderService,
                     Order order,
                     List<String> users,
                     boolean editable) {
        setWidthFull();
        // ---------- поля ----------
        IntegerField orderNumber = new IntegerField(t("order.number"));
        orderNumber.setWidthFull();

        TextField comment = new TextField(t("order.comment"));
        comment.setWidthFull();

        ComboBox<String> acceptedBy = new ComboBox<>(t("execution.acceptedBy"));
        acceptedBy.setItems(users);
        acceptedBy.setWidthFull();

        ComboBox<String> assignedTo = new ComboBox<>(t("execution.assignedTo"));
        assignedTo.setItems(users);
        assignedTo.setWidthFull();

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

        ComboBox<PaymentStatus> paymentStatus = new ComboBox<>(t("payment.status"));
        paymentStatus.setItems(PaymentStatus.values());
        paymentStatus.setItemLabelGenerator(this::t);
        paymentStatus.setWidthFull();

        IntegerField partiallyPaid = new IntegerField(t("payment.partiallyPaid"));
        partiallyPaid.setMin(0);
        partiallyPaid.setWidthFull();

        ComboBox<PaymentMethod> paymentMethod = new ComboBox<>(t("payment.method"));
        paymentMethod.setItems(PaymentMethod.values());
        paymentMethod.setItemLabelGenerator(this::t);
        paymentMethod.setWidthFull();

        ComboBox<OrderStatus> status = new ComboBox<>(t("execution.status"));
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
        VerticalLayout orderLeft = new VerticalLayout(orderNumber, dueDate);
        VerticalLayout orderRight = new VerticalLayout(comment, price);

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

        if (editable) {
            H3 orderTitle = new H3(t("order.title"));
            H3 executionTitle = new H3(t("execution.title"));
            H3 paymentTitle = new H3(t("payment.title"));

            formLayout.add(orderTitle,
                    orderBlock,
                    executionTitle,
                    executionBlock,
                    paymentTitle,
                    paymentBlock);
            // ---------- кнопки ----------
            Button saveBtn = new Button(t("button.save"), e -> {
                if (binder.validate().isOk()) {
                    binder.writeBeanIfValid(order);
                    orderService.save(order);
                    Notification.show(
                                    t("order.saved"),
                                    4000,
                                    Notification.Position.BOTTOM_END)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    UI.getCurrent().navigate(OrdersListView.ORDERS);
                }
            });

            Button cancelBtn = new Button(t("button.cancel"),
                    e -> UI.getCurrent().navigate(OrdersListView.ORDERS));
            cancelBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

            Button deleteBtn = new Button(t("button.delete"), e -> {
                if (order.getId() != null) {
                    orderService.delete(order);
                    Notification.show(
                                    t("order.deleted"),
                                    3000,
                                    Notification.Position.BOTTOM_END)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    UI.getCurrent().navigate(OrdersListView.ORDERS);
                }
            });
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
            HorizontalLayout buttons = new HorizontalLayout(saveBtn, cancelBtn);
            if (order.getId() != null) {
                buttons.add(deleteBtn);
            }
            formLayout.add(buttons);
        } else {
            formLayout.add(new H3(t("order.title")));
            formLayout.add(orderNumber, dueDate, price, status);
        }
        add(formLayout);
    }
}