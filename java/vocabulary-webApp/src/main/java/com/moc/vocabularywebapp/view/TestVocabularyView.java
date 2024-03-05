package com.moc.vocabularywebapp.view;

import com.moc.vocabularywebapp.constant.*;
import com.moc.vocabularywebapp.service.result.ResultService;
import com.moc.vocabularywebapp.service.statistic.VocabularyStatisticService;
import com.moc.vocabularywebapp.service.vocabulary.VocabularyService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.moc.vocabularywebapp.presenter.TestVocabularyPresenter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import java.util.*;

@PageTitle("Vokabeln lernen")
@Route(value= Constants.TEST_PATH, layout=MainView.class)
@AnonymousAllowed
public class TestVocabularyView extends VerticalLayout {

    private Locale locale;
    private ResourceBundle label;
    private ResourceBundle route;
    private ResourceBundle message;
    private TestVocabularyPresenter presenter;
    private final VocabularyService vocabularyService;
    private final VocabularyStatisticService vocabularyStatisticService;
    private final ResultService resultService;
    private TextArea expression;
    private TextField translation;
    private Button testButton;
    private Button helpButton;
    private Button solutionButton;
    private Button nextButton;
    private Button showResultButton;

    public TestVocabularyView(VocabularyService vocabularyService, ResultService resultService, VocabularyStatisticService vocabularyStatisticService) {
        this.vocabularyService = vocabularyService;
        this.resultService = resultService;
        this.vocabularyStatisticService = vocabularyStatisticService;
        this.locale = UI.getCurrent().getLocale();
        this.label = ResourceBundle.getBundle(ResourceBundleNames.LABLE_BUNDLE, locale);
        this.route = ResourceBundle.getBundle(ResourceBundleNames.ROUTE_BUNDLE, locale);
        this.message = ResourceBundle.getBundle(ResourceBundleNames.MESSAGE_BUNDLE, locale);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        createViewVariables();
        configureTextArea();
        this.presenter = new TestVocabularyPresenter(this, vocabularyService, resultService, vocabularyStatisticService);
        add(expression, translation, configureButtons());
    }

    private void createViewVariables() {
        expression = new TextArea(label.getString(LabelKeys.EXPRESSION_TABLE_HEADER));
        translation = new TextField(label.getString(LabelKeys.TRANSLATION_TABLE_HEADER));
        testButton = new Button(label.getString(LabelKeys.TEST_BUTTON));
        helpButton = new Button(label.getString(LabelKeys.HELP_BUTTON));
        solutionButton = new Button(label.getString(LabelKeys.SOLUTION_BUTTON));
        nextButton = new Button(label.getString(LabelKeys.NEXT_BUTTON));
        showResultButton = new Button(label.getString(LabelKeys.SHOW_RESULTS_BUTTON));
    }

    private void configureTextArea() {
        expression.setReadOnly(true);
    }

    private Component configureButtons() {

        testButton.addClickListener(event -> presenter.testVocabulary(translation.getValue()));
        helpButton.addClickListener(event -> presenter.getHelp());
        solutionButton.addClickListener(event -> presenter.showSolution());
        nextButton.addClickListener(event -> presenter.showNextVocabulary());
        showResultButton.addClickListener(event -> openTestResult());
        nextButton.setVisible(false);
        showResultButton.setVisible(false);
        return new HorizontalLayout(testButton, helpButton, solutionButton, nextButton, showResultButton);
    }

    public void setExpression(String expression) {this.expression.setValue(expression);
    }
    public void setTranslation(String translation) {
        this.translation.setValue(translation);
    }

    public void clearTranslationField() {
        translation.clear();
    }

    public void showHelp(String clue) {
        translation.setPlaceholder(clue);
        helpButton.setVisible(false);
    }

    public String buildHelp(){
        return label.getString(LabelKeys.DOT);
    }

    private void openTestResult() {
        getUI().ifPresent(ui -> UI.getCurrent().navigate(route.getString(RouteKeys.TEST_RESULT_ROUTE)));
    }

    public void updateButtonsForNextVocabulary() {
        // Logik zum Aktualisieren der Buttons, wenn die nächste Vokabel angezeigt wird
        translation.setPlaceholder("");
        nextButton.setVisible(false);
        testButton.setVisible(true);
        solutionButton.setVisible(true);
        helpButton.setVisible(true);
        translation.setReadOnly(false);
    }

    public void updateButtonsForSolution(boolean lastVocabulary) {
        // Logik zum Aktualisieren der Buttons, wenn die Lösung angezeigt wird
        translation.setPlaceholder("");
        testButton.setVisible(false);
        solutionButton.setVisible(false);
        helpButton.setVisible(false);
        translation.setReadOnly(true);
        if(lastVocabulary){
            nextButton.setVisible(false);
            updateButtonsForFinish();

        }else{
            nextButton.setVisible(true);
        }

    }

    private void updateButtonsForFinish(){
        nextButton.setVisible(false);
        testButton.setVisible(false);
        solutionButton.setVisible(false);
        helpButton.setVisible(false);
        nextButton.setVisible(false);
        showResultButton.setVisible(true);
        translation.setReadOnly(true);


    }

    public void showNoVocabularyFound() {
        Notification noVocabularyFound = Notification.
                show(message.getString(MessageKeys.NO_VOCABULARY_FOUND));
        noVocabularyFound.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        noVocabularyFound.setPosition(Notification.Position.TOP_START);
    }

    public void showTestFinished() {
        Notification testFinished = Notification.
                show(message.getString(MessageKeys.TEST_FINISHED));
        testFinished.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        testFinished.setPosition(Notification.Position.TOP_START);
        updateButtonsForFinish();
    }

    public void showCorrectAnswerNotification() {
        Notification correctAnswer = Notification.
                show(message.getString(MessageKeys.CORRECT_TRANSLATION));
        correctAnswer.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        correctAnswer.setPosition(Notification.Position.TOP_START);
    }

    public void showWrongAnswerNotification() {
        Notification wrongAnswer = Notification.
                show(message.getString(MessageKeys.WRONG_TRANSLATION));
        wrongAnswer.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        wrongAnswer.setPosition(Notification.Position.TOP_START);
    }

}