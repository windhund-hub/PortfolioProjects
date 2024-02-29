package com.moc.vocabularywebapp.view;

import com.moc.vocabularywebapp.constant.*;
import com.moc.vocabularywebapp.model.UserVocabularyReference;
import com.moc.vocabularywebapp.model.Vocabulary;
import com.moc.vocabularywebapp.service.vocabulary.VocabularyService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.data.selection.SelectionListener;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

@PageTitle("Vokabel aus DB hinzufügen")
@Route(value = Constants.ADD_DB_VOCABULARY_ROUTE, layout=MainView.class)
public class AddDBVocabularyView extends VerticalLayout implements SelectionListener<Grid<Vocabulary>,Vocabulary> {


    private Locale locale;
    private ResourceBundle label;
    private ResourceBundle route;
    private ResourceBundle message;
    private Grid<Vocabulary> grid;
    private final VocabularyService vocabularyService;
    private Button save;
    private Button cancel;
    private Set<Vocabulary> selected;

    public AddDBVocabularyView(VocabularyService vocabularyService){
        this.vocabularyService = vocabularyService;
        this.locale = UI.getCurrent().getLocale();
        this.label = ResourceBundle.getBundle(ResourceBundleNames.LABLE_BUNDLE, locale);
        this.route = ResourceBundle.getBundle(ResourceBundleNames.ROUTE_BUNDLE, locale);
        this.message = ResourceBundle.getBundle(ResourceBundleNames.MESSAGE_BUNDLE, locale);
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        createVariables();
        configureGrid();
        add(grid, createButton());
        loadVocabulary();
    }

    private void createVariables() {

        this.grid = new Grid<>(Vocabulary.class);
        this.save = new Button(label.getString(LabelKeys.SAVE_BUTTON));
        this.cancel = new Button(label.getString(LabelKeys.CLOSE_BUTTON));
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(Vocabulary::getExpression).setHeader(label.getString(LabelKeys.EXPRESSION_TABLE_HEADER));
        grid.addColumn(Vocabulary::getTranslation).setHeader(label.getString(LabelKeys.TRANSLATION_TABLE_HEADER));

        grid.getColumns().forEach(column -> column.setAutoWidth(true));
    }

    private Component createButton() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        cancel.addClickListener(event-> closeView());
        save.addClickListener(event -> saveSelected());

        return new HorizontalLayout(save, cancel);
    }

    private void closeView() {
        getUI().ifPresent(ui -> ui.navigate(route.getString(RouteKeys.ALL_VOCABULARY_ROUTE)));
    }

    private void loadVocabulary() {
        grid.setItems(vocabularyService.findAll());
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addSelectionListener(this);
    }

    private void saveSelected() {
        selected.forEach(vocabulary ->
                vocabularyService.saveSelectedVocabularyToUserList("123", vocabularyService.getVocabularyList(), vocabulary));
        Notification notification = Notification.show(message.getString(MessageKeys.VOCABULARY_SAVED));
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_CENTER);
        grid.setItems(vocabularyService.findAll());
    }
    @Override
    public void selectionChange(SelectionEvent<Grid<Vocabulary>,Vocabulary>selectionEvent) {
        this.selected = selectionEvent.getAllSelectedItems();
    }



}
