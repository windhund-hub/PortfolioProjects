package com.moc.vocabularywebapp.view;


import com.moc.vocabularywebapp.constant.LabelKeys;
import com.moc.vocabularywebapp.constant.MessageKeys;
import com.moc.vocabularywebapp.constant.ResourceBundleNames;
import com.moc.vocabularywebapp.constant.RouteKeys;
import com.moc.vocabularywebapp.service.user.SecurityService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;
import java.util.ResourceBundle;

@org.springframework.stereotype.Component
public class SignupViewFactory {

    @Autowired
    private SecurityService securityService;

    private class SignupForm {

        private VerticalLayout root;
        private TextField username;
        private PasswordField password;
        private PasswordField passwordAgain;
        private Button signup;
        private Button cancel;
        private  H2 title;
        private Locale locale;
        private ResourceBundle label;
        private ResourceBundle route;
        private ResourceBundle message;

        public SignupForm(){
            this.locale = UI.getCurrent().getLocale();
            this.label = ResourceBundle.getBundle(ResourceBundleNames.LABLE_BUNDLE, locale);
            this.route = ResourceBundle.getBundle(ResourceBundleNames.ROUTE_BUNDLE, locale);
            this.message = ResourceBundle.getBundle(ResourceBundleNames.MESSAGE_BUNDLE, locale);
        }

        public SignupForm init() {
            title = new H2(label.getString(LabelKeys.SIGNUP_TITLE));
            username = new TextField(label.getString(LabelKeys.USERNAME));
            password = new PasswordField(label.getString(LabelKeys.PASSWORD));
            passwordAgain = new PasswordField(label.getString(LabelKeys.PASSWORD_AGAIN));
            signup = new Button(label.getString(LabelKeys.SIGNUP_BUTTON));
            cancel = new Button(label.getString(LabelKeys.CLOSE_BUTTON));


            signup.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            root = new VerticalLayout();
            root.setAlignItems(Alignment.CENTER);
            root.setJustifyContentMode(JustifyContentMode.CENTER);
            root.setMargin(true);

            return this;
        }

        public Component layout() {
            root.add(title);
            root.add(username);
            root.add(password);
            root.add(passwordAgain);
            root.add(new HorizontalLayout(signup, cancel));

            cancel.addClickListener(e -> {
                cancel.getUI().ifPresent(ui -> ui.navigate(route.getString(RouteKeys.LOGIN_ROUTE)));
            });

            signup.addClickListener(e -> {
                if(!password.getValue().isEmpty() && password.getValue().equals(passwordAgain.getValue())) {
                    securityService.save(username.getValue(), password.getValue());
                    signup.getUI().ifPresent(ui -> ui.navigate(route.getString(RouteKeys.LOGIN_ROUTE)));
                } else {
                    Notification.show(message.getString(MessageKeys.PASSWORD_NOT_IDENTICAL));
                }
            });

            return root;
        }
    }

    public Component create() {
        return new SignupForm().init().layout();
    }
}
