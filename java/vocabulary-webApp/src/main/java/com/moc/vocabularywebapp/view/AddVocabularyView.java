package com.moc.vocabularywebapp.view;

import com.moc.vocabularywebapp.constant.*;
import com.moc.vocabularywebapp.model.UserVocabulary;
import com.moc.vocabularywebapp.presenter.AddVocabularyPresenter;
import com.moc.vocabularywebapp.service.vocabulary.VocabularyService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.Locale;
import java.util.ResourceBundle;


@PageTitle("Vokabel hinzufügen")
@Route(value = Constants.ADD_VOCABULARY_ROUTE, layout=MainView.class)
public class AddVocabularyView extends VerticalLayout {

    private Locale locale;
    private ResourceBundle label;
    private ResourceBundle route;
    private ResourceBundle message;
    private VocabularyService vocabularyService;
    private AddVocabularyPresenter presenter;
    private TextField expression;
    private TextField translation;
    private Button saveButton;
    private Button closeButton;
    private Binder<UserVocabulary> binder;

    public AddVocabularyView(VocabularyService vocabularyService){
        this.vocabularyService = vocabularyService;
        this.locale = UI.getCurrent().getLocale();
        this.label = ResourceBundle.getBundle(ResourceBundleNames.LABLE_BUNDLE, locale);
        this.route = ResourceBundle.getBundle(ResourceBundleNames.ROUTE_BUNDLE, locale);
        this.message = ResourceBundle.getBundle(ResourceBundleNames.MESSAGE_BUNDLE, locale);
        setAlignItems(Alignment.CENTER);
        createVariables();
        this.presenter = new AddVocabularyPresenter(this, vocabularyService);
        add(createFormLayout());
    }

    private void createVariables() {
        expression = new TextField(label.getString(LabelKeys.EXPRESSION_TABLE_HEADER));
        translation = new TextField(label.getString(LabelKeys.TRANSLATION_TABLE_HEADER));
        saveButton = new Button(label.getString(LabelKeys.SAVE_BUTTON));
        closeButton = new Button(label.getString(LabelKeys.CLOSE_BUTTON));

    }

    private Component createButtons() {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        saveButton.addClickListener(event -> presenter.saveVocabulary());

        closeButton.addClickListener(event -> closeView());

        return new HorizontalLayout(saveButton, closeButton);

    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        expression.setReadOnly(false);
        translation.setReadOnly(false);
        formLayout.add(expression,translation, createButtons());
        return formLayout;

    }

    public void createBinder(UserVocabulary userVocabulary) {
        try {
            binder = new BeanValidationBinder<>(UserVocabulary.class);
            binder.writeBean(userVocabulary);
            Notification notification = Notification.show(message.getString(MessageKeys.VOCABULARY_SAVED));
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.TOP_CENTER);

        }catch(ValidationException e){ e.printStackTrace();}
    }

    public String getExpressionTF(){
        return expression.getValue();
    }

    public String getTranslationTF(){
        return translation.getValue();
    }

    public void clearFields() {
        expression.clear();
        translation.clear();
    }

    private void closeView() {

        getUI().ifPresent(ui -> ui.navigate(route.getString(RouteKeys.ALL_VOCABULARY_ROUTE)));
    }
}
