package com.atelie;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.function.Consumer;

public class Notifications {
    public static void error(String message) {
        show(message, VaadinIcon.WARNING, NotificationVariant.LUMO_ERROR);
    }

    public static void success(String message) {
        show(message, VaadinIcon.CHECK, NotificationVariant.LUMO_SUCCESS);
    }

    public static void info(String message) {
        show(message, VaadinIcon.INFO_CIRCLE, NotificationVariant.LUMO_PRIMARY);
    }

    public static void warning(String message) {
        show(message, VaadinIcon.WARNING, NotificationVariant.LUMO_CONTRAST);
    }

    public static void show(String message,
                            VaadinIcon iconType,
                            NotificationVariant variant) {

        Notification n = new Notification();
        if (variant == NotificationVariant.LUMO_ERROR) {
            n.setDuration(0);
        } else {
            n.setDuration(7000);
        }
        n.setPosition(Notification.Position.BOTTOM_END);

        // --- icon ---
        Icon icon = iconType.create();

        // --- text ---
        Span text = new Span(message);

        // --- close button ---
        Button close = new Button(VaadinIcon.CLOSE_SMALL.create(), e -> n.close());
        close.addThemeVariants(ButtonVariant.LUMO_LARGE);

        // --- layout ---
        HorizontalLayout layout = new HorizontalLayout(icon, text, close);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.expand(text);

        n.add(layout);

        // --- theme ---
        n.addThemeVariants(variant);
        n.getElement().getThemeList().add("contrast");

        n.open();
    }

    public static void custom(String message,
                              NotificationVariant variant,
                              Consumer<Notification> customizer) {
        Notification n = new Notification();
        n.setPosition(Notification.Position.MIDDLE);
        n.setDuration(0);
        n.addThemeVariants(variant);

        Span text = new Span(message);
        text.getStyle().set("marginRight", "1em");

        HorizontalLayout layout = new HorizontalLayout(text);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        n.add(layout);

        if (customizer != null) {
            customizer.accept(n);
        }

        n.open();
    }
}