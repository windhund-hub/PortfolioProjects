package com.moc.vocabularywebapp.view;


import com.moc.vocabularywebapp.constant.LabelKeys;
import com.moc.vocabularywebapp.constant.ResourceBundleNames;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.moc.vocabularywebapp.constant.Constants;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import java.util.Locale;
import java.util.ResourceBundle;


@PageTitle("Signup")
@Route(value = Constants.SIGNUP_PATH, layout=MainView.class)
@AnonymousAllowed
public class SignupView extends VerticalLayout {

    private Locale locale;
    private ResourceBundle label;

    public SignupView(SignupViewFactory signupFactory) {
        this.locale = UI.getCurrent().getLocale();
        this.label = ResourceBundle.getBundle(ResourceBundleNames.LABLE_BUNDLE, locale);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);


        add(new H1(label.getString(LabelKeys.APP_TITLE)), signupFactory.create());
    }
}
