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
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class OrderForm extends AbstractView {

    private final Binder<Order> binder = new Binder<>(Order.class);

    public OrderForm(OrderService orderService, Order order, List<String> users, Runnable onSave) {

        setWidth("600px");

        // Поля
        IntegerField orderNumber = new IntegerField("Номер квитанции");
        TextField comment = new TextField("Комментарий");

        ComboBox<String> acceptedBy = new ComboBox<>("Принял заказ");
        acceptedBy.setItems(users);

        ComboBox<String> assignedTo = new ComboBox<>("Исполнитель");
        assignedTo.setItems(users);

        acceptedBy.addValueChangeListener(e -> {
            if (e.getValue() != null && assignedTo.getValue() == null) {
                assignedTo.setValue(e.getValue());
            }
        });

        DateTimePicker dueDate = new DateTimePicker("Срок исполнения");
        dueDate.setMin(LocalDateTime.now());
        dueDate.setStep(Duration.ofHours(1));

        IntegerField price = new IntegerField("Стоимость");
        price.setMin(0);

        ComboBox<PaymentStatus> paymentStatus = new ComboBox<>("Оплачено");
        paymentStatus.setItems(PaymentStatus.values());
        paymentStatus.setItemLabelGenerator(this::t);

        IntegerField partiallyPaid = new IntegerField("Аванс");
        partiallyPaid.setMin(0);

        paymentStatus.addValueChangeListener(e -> {
            boolean partially = e.getValue() == PaymentStatus.PARTIALLY;
            partiallyPaid.setVisible(partially);
            if (!partially) partiallyPaid.setValue(0);
        });

        ComboBox<PaymentMethod> paymentMethod = new ComboBox<>("Способ оплаты");
        paymentMethod.setItems(PaymentMethod.values());
        paymentMethod.setItemLabelGenerator(this::t);

        ComboBox<OrderStatus> status = new ComboBox<>("Статус");
        status.setItems(OrderStatus.values());
        status.setItemLabelGenerator(this::t);

        // Binder
        binder.forField(orderNumber).asRequired("Введите номер квитанции").bind(Order::getOrderNumber, Order::setOrderNumber);
        binder.forField(comment).bind(Order::getComment, Order::setComment);
        binder.forField(dueDate)
                .asRequired("Укажите срок исполнения")
                .withValidator(dt -> dt.getHour() >= 9 && dt.getHour() <= 21, "Время 9–21")
                .bind(
                        o -> o.getDueDate() != null
                                ? LocalDateTime.ofInstant(o.getDueDate(), ZoneId.systemDefault())
                                : null,
                        (o, v) -> o.setDueDate(v != null ? v.atZone(ZoneId.systemDefault()).toInstant() : null)
                );
        binder.forField(price).asRequired("Введите стоимость").bind(Order::getPrice, Order::setPrice);
        binder.forField(paymentStatus).bind(Order::getPaymentStatus, Order::setPaymentStatus);
        binder.forField(partiallyPaid).bind(Order::getPartiallyPaid, Order::setPartiallyPaid);
        binder.forField(paymentMethod).bind(Order::getPaymentMethod, Order::setPaymentMethod);
        binder.forField(status).bind(Order::getStatus, Order::setStatus);
        binder.forField(acceptedBy).bind(Order::getAcceptedBy, Order::setAcceptedBy);
        binder.forField(assignedTo).bind(Order::getAssignedTo, Order::setAssignedTo);

        binder.readBean(order);

        // Layout
        FormLayout form = new FormLayout();
        form.add(orderNumber,
                comment,
                acceptedBy,
                assignedTo,
                dueDate,
                price,
                paymentStatus,
                partiallyPaid,
                paymentMethod,
                status);

        Button saveBtn = new Button("Сохранить", e -> {
            if (binder.validate().isOk()) {
                binder.writeBeanIfValid(order);
                orderService.save(order);
                onSave.run();
                Notification.show("Заказ сохранен", 5000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                UI.getCurrent().navigate(OrdersListView.ORDERS);
            }
        });

        Button cancelBtn = new Button("Отмена",
                e -> UI.getCurrent().navigate(OrdersListView.ORDERS));
        cancelBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button deleteBtn = new Button("Удалить", e -> {
            if (order.getId() != null) {
                orderService.delete(order);
                Notification.show("Заказ удален", 3000,
                                Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                UI.getCurrent().navigate(OrdersListView.ORDERS);
            }
        });
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout buttons = new HorizontalLayout(saveBtn, cancelBtn);
        if (order.getId() != null) buttons.add(deleteBtn);

        add(form, buttons);
    }
}