package com.moc.vocabularywebapp.view;

import com.moc.vocabularywebapp.constant.Constants;
import com.moc.vocabularywebapp.constant.LabelKeys;
import com.moc.vocabularywebapp.constant.ResourceBundleNames;
import com.moc.vocabularywebapp.presenter.StudyVocabularyPresenter;
import com.moc.vocabularywebapp.service.vocabulary.VocabularyService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.*;

@PageTitle("Vokabeln lernen")
@Route(value= Constants.STUDY_PATH, layout=MainView.class)
public class StudyVocabularyView extends VerticalLayout {

    private Locale locale;
    private ResourceBundle label;
    private ResourceBundle route;
    private final VocabularyService vocabularyService;
    private StudyVocabularyPresenter presenter;
    private TextArea expression;
    private TextArea translation;
    private Button nextButton;
    private Button backButton;


    public StudyVocabularyView(VocabularyService vocabularyService) {

        this.vocabularyService = vocabularyService;
        this.locale = UI.getCurrent().getLocale();
        this.label = ResourceBundle.getBundle(ResourceBundleNames.LABLE_BUNDLE, locale);
        this.route = ResourceBundle.getBundle(ResourceBundleNames.ROUTE_BUNDLE, locale);
        setPadding(false);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        createViewVariables();
        configureTextArea();
        backButton.setVisible(false);
        this.presenter = new StudyVocabularyPresenter(vocabularyService, this);
        add(expression, translation,configureButtons());

    }
    private void createViewVariables() {
        expression = new TextArea(label.getString(LabelKeys.EXPRESSION_TABLE_HEADER));
        translation = new TextArea(label.getString(LabelKeys.TRANSLATION_TABLE_HEADER));
        nextButton = new Button(label.getString(LabelKeys.NEXT_BUTTON));
        backButton = new Button(label.getString(LabelKeys.BACK_BUTTON));
    }

    private void configureTextArea() {
        expression.setReadOnly(true);
        translation.setReadOnly(true);
    }

    private Component configureButtons() {
        nextButton.addClickListener(event -> presenter.showNextVocabulary());
        backButton.addClickListener(event -> presenter.showPreviousVocabulary());

        return new HorizontalLayout(backButton, nextButton);
    }

    public void setExpression(String word) {
        this.expression.setValue(word);
    }

    public void setTranslation(String translation) {
        this.translation.setValue(translation);
    }

    public void setBackButtonVisibility(boolean b) {
        backButton.setVisible(b);
    }

    public void setNextButtonVisibility(boolean b) {
        nextButton.setVisible(b);
    }
}
