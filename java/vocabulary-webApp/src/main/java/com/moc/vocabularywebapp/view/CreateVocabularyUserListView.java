package com.moc.vocabularywebapp.view;


import com.moc.vocabularywebapp.constant.Constants;
import com.moc.vocabularywebapp.constant.LabelKeys;
import com.moc.vocabularywebapp.constant.ResourceBundleNames;
import com.moc.vocabularywebapp.constant.RouteKeys;
import com.moc.vocabularywebapp.presenter.CreateVocabularyListPresenter;
import com.moc.vocabularywebapp.service.vocabulary.VocabularyService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
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
    private TextField listNameTextField;


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
        listNameTextField = new TextField(label.getString(LabelKeys.LISTNAME));
    }

    private Component configureButtons() {
        createListButton.addClickListener(event -> presenter.saveList());
        addVocabularyButton.addClickListener(event -> addVocabulary());
        addVocabularyButton.setEnabled(false);
        return new HorizontalLayout(createListButton, addVocabularyButton);
    }

    public void setButtonsForAddVocabulary(){
        createListButton.setEnabled(false);
        addVocabularyButton.setEnabled(true);
    }

    private void addVocabulary() {
        //getUI().ifPresent(ui -> UI.getCurrent().navigate(route.getString(RouteKeys.ADD_VOCABULARY_ROUTE)));
    }
}
