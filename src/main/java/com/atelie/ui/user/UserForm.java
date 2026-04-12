package com.atelie.ui.user;

import com.atelie.db.user.Role;
import com.atelie.db.user.User;
import com.atelie.db.user.UserStatus;
import com.atelie.service.user.UserService;
import com.atelie.ui.AbstractView;
import com.atelie.ui.Notifications;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ModalityMode;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import static com.atelie.ui.user.UsersListView.USERS;

public class UserForm extends AbstractView {
    private final Binder<User> binder = new Binder<>(User.class);

    public UserForm(UserService userService,
                    User user) {
        setWidthFull();
        // ---------- поля ----------
        TextField username = new TextField(t("user.username"));
        username.setWidthFull();

        TextField firstname = new TextField(t("user.firstname"));
        firstname.setWidthFull();

        TextField lastname = new TextField(t("user.lastname"));
        lastname.setWidthFull();

        TextField password = new TextField(t("user.password"));
        password.setWidthFull();

        ComboBox<UserStatus> status = new ComboBox<>(t("user.status"));
        status.setItems(UserStatus.ACTIVE, UserStatus.BLOCKED);
        status.setItemLabelGenerator(this::t);
        status.setWidthFull();

        ComboBox<Role> role = new ComboBox<>(t("user.role"));
        role.setItems(Role.USER, Role.OWNER, Role.ADMIN);
        role.setItemLabelGenerator(this::t);
        role.setWidthFull();

        boolean isAdmin = user.getRole() == Role.ADMIN;
        // ---------- binder ----------
        binder.forField(username)
                .asRequired(t("validation.usernameRequired"))
                .bind(User::getUsername, User::setUsername);
        binder.forField(firstname)
                .asRequired(t("validation.firstnameRequired"))
                .bind(User::getFirstName, User::setFirstName);
        binder.forField(lastname)
                .asRequired(t("validation.lastnameRequired"))
                .bind(User::getLastName, User::setLastName);
        binder.forField(password)
                .asRequired(t("validation.passwordRequired"))
                .bind(User::getPassword, User::setPassword);
        binder.forField(status)
                .bind(User::getStatus, isAdmin ? null : User::setStatus);
        binder.forField(role)
                .bind(User::getRole, isAdmin ? null : User::setRole);
        binder.readBean(user);

        // ---------- layout ----------
        VerticalLayout userLeft = new VerticalLayout(username, firstname, lastname);
        VerticalLayout userRight = new VerticalLayout();
        if (user.getPassword() == null) {
            userRight.add(password);
        }
        userRight.add(role);
        userRight.add(status);

        HorizontalLayout userBlock = new HorizontalLayout(userLeft, userRight);
        userBlock.setWidthFull();
        userBlock.setFlexGrow(1, userLeft, userRight);

        // ---------- кнопки ----------
        Button saveBtn = new Button(t("button.save"), event -> {
            if (!binder.validate().isOk()) {
                return;
            }
            binder.writeBeanIfValid(user);
            userService.findUser(user.getUsername(), user.getId()).ifPresentOrElse(existingUser -> {
                Dialog dialog = new Dialog();
                dialog.add(new Text(t("user.duplicateUser", user.getUsername())));

                Button cancel = new Button(t("button.cancel"), e -> dialog.close());

                HorizontalLayout actions = new HorizontalLayout(cancel);
                actions.setSpacing(true);
                actions.setPadding(true);
                dialog.add(actions);
                dialog.setModality(ModalityMode.STRICT);
                dialog.setCloseOnOutsideClick(true);
                dialog.open();
            }, () -> saveUser(userService, user));
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button(t("button.cancel"),
                e -> UI.getCurrent().navigate(USERS));
        cancelBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        Button deleteBtn = new Button(t("button.delete"), e -> {
            Dialog dialog = new Dialog();

            H3 label = new H3(t("user.deletion", user.getUsername()));
            Button save = new Button(t("button.save"), ev -> {
                user.setStatus(UserStatus.DELETED);
                userService.save(user);
                Notifications.warning(t("user.deleted", user.getUsername()));
                UI.getCurrent().navigate(USERS);
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
        Button changePasswordBtn = new Button(t("user.changePassword"), e -> changePasswordDialog(this, userService, user));
        changePasswordBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
        HorizontalLayout buttons = new HorizontalLayout(saveBtn);
        if (user.getId() != null) {
            buttons.add(changePasswordBtn);
        }
        if (user.getId() != null && !isAdmin) {
            buttons.add(deleteBtn);
        }
        buttons.add(cancelBtn);
        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setWidthFull();

        formLayout.add(userBlock);
        formLayout.add(buttons);
        add(formLayout);
    }

    private void saveUser(UserService userService, User user) {
        userService.save(user);
        Notifications.info(t("user.saved", user.getUsername()));
        UI.getCurrent().navigate(USERS);
    }

    public static void changePasswordDialog(Component component,
                                            UserService userService,
                                            User user) {
        Dialog dialog = new Dialog();

        TextField newPass1 = new TextField(component.getTranslation("user.newPassword"));
        newPass1.setRequiredIndicatorVisible(true);
        TextField newPass2 = new TextField(component.getTranslation("user.repeatNewPassword"));
        newPass2.setRequiredIndicatorVisible(true);
        Button save = new Button(component.getTranslation("button.save"), ev -> {
            String p1 = newPass1.getValue();
            String p2 = newPass2.getValue();

            if (p1 == null || p1.isBlank()) {
                Notifications.error(component.getTranslation("validation.passwordRequired"));
                return;
            }

            if (!p1.equals(p2)) {
                Notifications.error(component.getTranslation("validation.passwordMismatch"));
                return;
            }
            user.setPassword(newPass1.getValue());
            userService.save(user);
            Notifications.info(component.getTranslation("user.passwordChanged", user.getUsername()));
            dialog.close();
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button(component.getTranslation("button.cancel"), ev -> dialog.close());

        VerticalLayout layout = new VerticalLayout(newPass1, newPass2);
        HorizontalLayout buttonLayout = new HorizontalLayout(save, cancel);
        buttonLayout.setWidthFull();
        buttonLayout.setFlexGrow(1, save, cancel);
        layout.add(buttonLayout);
        dialog.add(layout);
        dialog.open();
    }
}