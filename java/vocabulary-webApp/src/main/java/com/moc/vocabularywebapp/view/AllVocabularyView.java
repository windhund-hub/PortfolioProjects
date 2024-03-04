package com.moc.vocabularywebapp.view;

import com.moc.vocabularywebapp.constant.Constants;
import com.moc.vocabularywebapp.constant.LabelKeys;
import com.moc.vocabularywebapp.constant.ResourceBundleNames;
import com.moc.vocabularywebapp.constant.RouteKeys;
import com.moc.vocabularywebapp.model.Vocabulary;
import com.moc.vocabularywebapp.model.VocabularyStatistic;
import com.moc.vocabularywebapp.presenter.AllVocabularyPresenter;
import com.moc.vocabularywebapp.service.vocabulary.VocabularyService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

//TODO add vocabulary entfernen -> eingenen Vikableliste
//Sprachspalte einfügen, action für Sprachbutton
@PageTitle("Alle Vokabeln")
@Route(value= Constants.ALL_VOCABULARY_PATH, layout=MainView.class)
@AnonymousAllowed
public class AllVocabularyView extends VerticalLayout {

    private Locale locale;
    private ResourceBundle label;
    private ResourceBundle route;
    private final VocabularyService vocabularyService;
    private final AllVocabularyPresenter presenter;
    private Grid<Vocabulary> grid;
    private MenuBar menuBar;
    private TextField filterExpression;
    private TextField filterTranslation;
    private HorizontalLayout horizontalLayout;
    private Button addVocabularyButton;

    public AllVocabularyView(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
        this.locale = UI.getCurrent().getLocale();
        this.label = ResourceBundle.getBundle(ResourceBundleNames.LABLE_BUNDLE, locale);
        this.route = ResourceBundle.getBundle(ResourceBundleNames.ROUTE_BUNDLE, locale);
        setSizeFull();
        setAlignItems(Alignment.START);
        createViewVariables();
        configureGrid();
        createToolBar();
        createMenu();
        this.presenter = new AllVocabularyPresenter(this, vocabularyService);
        addVocabularyButton.setEnabled(false);
        menuBar.setEnabled(false);
        add(horizontalLayout, grid);
    }

    private void createViewVariables(){
        this.grid = new Grid<>();
        this.menuBar = new MenuBar();
        this.filterExpression = new TextField();
        this.filterTranslation = new TextField();
        this.horizontalLayout = new HorizontalLayout();
        this.addVocabularyButton = new Button(label.getString(LabelKeys.ADD_VOCABULARY_BUTTON));
    }

    private void createToolBar() {
        filterExpression.setPlaceholder(label.getString(LabelKeys.PLACEHOLDER_FILTER_EXPRESSION));
        filterExpression.setClearButtonVisible(true);
        filterExpression.setValueChangeMode(ValueChangeMode.LAZY);

        filterTranslation.setPlaceholder(label.getString(LabelKeys.PLACEHOLDER_FILTER_TRANSLATION));
        filterTranslation.setClearButtonVisible(true);
        filterTranslation.setValueChangeMode(ValueChangeMode.LAZY);


        filterExpression.addValueChangeListener(event -> presenter.filterExpression(event.getValue()));
        filterTranslation.addValueChangeListener(event -> presenter.filterTranslation(event.getValue()));
        addVocabularyButton.addClickListener(event ->
                getUI().ifPresent(ui -> ui.navigate(route.getString(RouteKeys.ADD_VOCABULARY_ROUTE))));
    }

    private Component createMenuBar() {

        MenuItem language = menuBar.addItem(label.getString(LabelKeys.CHANGE_LANGUAGE_BUTTON));
        SubMenu languageSubMenu = language.getSubMenu();

        languageSubMenu.addItem(label.getString(LabelKeys.FRENCH_SUBMENU));
        languageSubMenu.addItem(label.getString(LabelKeys.ENGLISH_US_SUBMENU));
        languageSubMenu.addItem(label.getString(LabelKeys.ENGLISH_GB_SUBMENU));

        return menuBar;

    }
    private void createMenu(){
        horizontalLayout.add(filterExpression,filterTranslation, createMenuBar(), addVocabularyButton);
    }

    public void updateGrid(List<Vocabulary> vocabularies) {
        grid.setItems(vocabularies);
    }

   private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(Vocabulary::getExpression).setHeader(label.getString(LabelKeys.EXPRESSION_TABLE_HEADER));
        grid.addColumn(Vocabulary::getTranslation).setHeader(label.getString(LabelKeys.TRANSLATION_TABLE_HEADER));
        grid.addColumn(vocabulary -> {
            VocabularyStatistic vocabularyStatistic = vocabulary.getVocabularyStatistic();
            return vocabularyStatistic!= null ? vocabularyStatistic.getNumberOfTraining():label.getString(LabelKeys.NOT_ACCESSIBLE);}).setHeader(label.getString(LabelKeys.TESTED_TABLE_HEADER));
        grid.addColumn(vocabulary -> {
            VocabularyStatistic vocabularyStatistic = vocabulary.getVocabularyStatistic();
            return vocabularyStatistic!= null ? vocabularyStatistic.getNumberOfSuccess():label.getString(LabelKeys.NOT_ACCESSIBLE);}).setHeader(label.getString(LabelKeys.SUCCESS_TABLE_HEADER));
       grid.addColumn(vocabulary -> {
           VocabularyStatistic vocabularyStatistic = vocabulary.getVocabularyStatistic();
           return vocabularyStatistic!= null ? vocabularyStatistic.getSuccessRate():label.getString(LabelKeys.NOT_ACCESSIBLE);}).setHeader(label.getString(LabelKeys.SUCCES_RATE_TABLE_HEADER));


       grid.getColumns().forEach(column -> column.setAutoWidth(true));

    }
}

