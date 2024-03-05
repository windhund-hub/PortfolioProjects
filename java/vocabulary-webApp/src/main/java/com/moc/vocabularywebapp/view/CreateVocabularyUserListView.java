package com.moc.vocabularywebapp.view;

import com.moc.vocabularywebapp.constant.*;
import com.moc.vocabularywebapp.model.UserVocabularyList;
import com.moc.vocabularywebapp.presenter.CreateVocabularyListPresenter;
import com.moc.vocabularywebapp.service.vocabulary.VocabularyService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
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

@PageTitle("Vokabelliste erstellen")
@Route(value= Constants.CREATE_VOCABULARY_LIST, layout=MainView.class)
public class CreateVocabularyUserListView extends VerticalLayout {

    private Locale locale;
    private ResourceBundle label;
    private ResourceBundle route;
    private ResourceBundle message;
    private final CreateVocabularyListPresenter presenter;
    private final VocabularyService vocabularyService;
    private Button createListButton;
    private Button addVocabularyButton;
    private Button addDBVocabularyButton;
    private TextField listNameTextField;
    private Binder<UserVocabularyList> binder;


    public CreateVocabularyUserListView(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
        this.locale = UI.getCurrent().getLocale();
        this.label = ResourceBundle.getBundle(ResourceBundleNames.LABLE_BUNDLE, locale);
        this.route = ResourceBundle.getBundle(ResourceBundleNames.ROUTE_BUNDLE, locale);
        this.message = ResourceBundle.getBundle(ResourceBundleNames.MESSAGE_BUNDLE, locale);
        createVariables();
        this.presenter = new CreateVocabularyListPresenter(this, vocabularyService);
        add(listNameTextField, configureButtons());
    }

    private void createVariables() {
        createListButton = new Button(label.getString(LabelKeys.CREATE_LIST_BUTTON));
        addVocabularyButton = new Button(label.getString(LabelKeys.ADD_VOCABULARY_BUTTON));
        addDBVocabularyButton = new Button(label.getString(LabelKeys.ADD_DB_VOCABULARY_BUTTON));
        listNameTextField = new TextField(label.getString(LabelKeys.LISTNAME));
    }

    private Component configureButtons() {
        createListButton.addClickListener(event -> presenter.saveList());
        addVocabularyButton.addClickListener(event -> addVocabulary());
        addDBVocabularyButton.addClickListener(event -> addDBVocabulary());
        addVocabularyButton.setEnabled(false);
        addDBVocabularyButton.setEnabled(false);
        return new HorizontalLayout(createListButton, addVocabularyButton, addDBVocabularyButton);
    }


    public void setButtonsForAddVocabulary(){
        createListButton.setEnabled(false);
        addVocabularyButton.setEnabled(true);
        addDBVocabularyButton.setEnabled(true);
    }

    //listenname übergeben
    private void addVocabulary() {
        getUI().ifPresent(ui -> UI.getCurrent().navigate(route.getString(RouteKeys.ADD_VOCABULARY_ROUTE)));
    }

    private void addDBVocabulary() {
        getUI().ifPresent(ui -> UI.getCurrent().navigate(route.getString(RouteKeys.ADD_DB_VOCABULARY_ROUTE)));
    }

    public void createBinder(UserVocabularyList userVocabularyList) {
        try {
            binder = new BeanValidationBinder<>(UserVocabularyList.class);
            binder.writeBean(userVocabularyList);
        }catch(ValidationException e){ e.printStackTrace();}
    }

    public String setListNameFromTF(){
        return listNameTextField.getValue();
    }

    public void showListCreated(){
        Notification listCreated = Notification.
                show(message.getString(MessageKeys.LIST_CREATED));
        listCreated.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        listCreated.setPosition(Notification.Position.TOP_START);
    }
}
