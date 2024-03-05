package com.moc.vocabularywebapp.view;

import com.moc.vocabularywebapp.constant.LabelKeys;
import com.moc.vocabularywebapp.constant.MessageKeys;
import com.moc.vocabularywebapp.constant.ResourceBundleNames;
import com.moc.vocabularywebapp.constant.RouteKeys;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Locale;
import java.util.ResourceBundle;

@org.springframework.stereotype.Component
public class LoginViewFactory {

    @Autowired
    private DaoAuthenticationProvider authenticationProvider;


    private class LoginForm {

        private Locale locale;
        private ResourceBundle label;
        private ResourceBundle route;
        private ResourceBundle message;
        private VerticalLayout root;
        private TextField username;
        private PasswordField password;
        private Button login;
        private Button signup;
        private H2 title;

        public LoginForm(){
            this.locale = UI.getCurrent().getLocale();
            this.label = ResourceBundle.getBundle(ResourceBundleNames.LABLE_BUNDLE, locale);
            this.route = ResourceBundle.getBundle(ResourceBundleNames.ROUTE_BUNDLE, locale);
            this.message = ResourceBundle.getBundle(ResourceBundleNames.MESSAGE_BUNDLE, locale);
        }

        public LoginForm init(){
            title = new H2(label.getString(LabelKeys.LOGIN_TITLE));

            username = new TextField(label.getString(LabelKeys.USERNAME));
            password = new PasswordField(label.getString(LabelKeys.PASSWORD));

            login = new Button(label.getString(LabelKeys.LOGIN_TITLE));
            signup = new Button(label.getString(LabelKeys.SIGNUP_BUTTON));

            login.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            signup.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            login.addClickListener(event->login());
            signup.addClickListener(event->signup());

            root = new VerticalLayout();
            root.setAlignItems(FlexComponent.Alignment.CENTER);
            root.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            root.setMargin(true);

            return this;
        }

        private void signup() {
            login.getUI().ifPresent(ui -> ui.navigate(route.getString(RouteKeys.SIGNUP_ROUTE)));
        }

        private void login() {
            try {
                Authentication auth = new UsernamePasswordAuthenticationToken(username.getValue(), password.getValue());
                Authentication authenticated = authenticationProvider.authenticate(auth);
                SecurityContextHolder.getContext().setAuthentication(authenticated);
                login.getUI().ifPresent(ui -> ui.navigate(""));
            }catch (AuthenticationException e) {
                Notification notification = Notification.
                        show(message.getString(MessageKeys.INCORRECT_CREDENTIALS));
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setPosition(Notification.Position.TOP_CENTER);
                e.printStackTrace();
            }
        }

        public Component layout(){
            root.add(title);
            root.add(username);
            root.add(password);
            root.add(new HorizontalLayout(login, signup));
            return root;
        }

    }

    public Component create(){
        return new LoginForm().init().layout();
    }

}
