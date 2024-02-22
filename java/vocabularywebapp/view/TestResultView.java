package com.moc.vocabularywebapp.view;

import com.moc.vocabularywebapp.constant.Constants;
import com.moc.vocabularywebapp.constant.ResourceBundleNames;
import com.moc.vocabularywebapp.constant.RouteKeys;
import com.moc.vocabularywebapp.model.Vocabulary;
import com.moc.vocabularywebapp.presenter.TestResultPresenter;
import com.moc.vocabularywebapp.service.result.ResultService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.Map;
import java.util.*;
import com.moc.vocabularywebapp.constant.LabelKeys;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Testresultat")
@Route(value= Constants.RESULT_PATH, layout=MainView.class)
public class TestResultView extends VerticalLayout {

    private TestResultPresenter presenter;
    private Locale locale;
    private ResourceBundle label;
    private ResourceBundle route;
    private final ResultService resultService;
    private Grid<Map.Entry<Vocabulary, String>> grid;
    private Button testAgainButton;

    public TestResultView(ResultService resultService) {
        this.resultService = resultService;
        this.locale = UI.getCurrent().getLocale();
        this.label = ResourceBundle.getBundle(ResourceBundleNames.LABLE_BUNDLE, locale);
        this.route = ResourceBundle.getBundle(ResourceBundleNames.ROUTE_BUNDLE, locale);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        createVariables();
        this.presenter = new TestResultPresenter(this, resultService);
        configureGrid();
        createToolbar();
        add(grid, testAgainButton);
    }

    private void createVariables() {
        grid = new Grid<>();
        testAgainButton = new Button(label.getString(LabelKeys.REPEAT_TEST_BUTTON));
    }

    private void configureGrid() {


        grid.setSizeFull();
        grid.addColumn(entry -> (entry.getKey()).getExpression()).setHeader(label.getString(LabelKeys.EXPRESSION_TABLE_HEADER));
        grid.addColumn(entry -> (entry.getKey()).getTranslation()).setHeader(label.getString(LabelKeys.TRANSLATION_TABLE_HEADER));
        grid.addComponentColumn(entry -> {
            Icon icon;
            if(entry.getValue().equals("true")){

                icon = VaadinIcon.CHECK_CIRCLE.create();
                icon.setColor("green");
            }else{
                icon = VaadinIcon.CLOSE_CIRCLE.create();
                icon.setColor("red");
            }
            return icon;
        }).setHeader(label.getString(LabelKeys.RESULT_TABLE_HEADER));

        //Map.Entry::getValue).setHeader(label.getString(LabelKeys.RESULT_TABLE_HEADER));

        add(grid);
    }

    public void updateGrid(Set<Map.Entry<Vocabulary, String>> results) {
        grid.setItems(results);
    }

    private void createToolbar() {
        testAgainButton.addClickListener(event -> getUI().ifPresent(ui ->ui.navigate(route.getString(RouteKeys.TEST_VOCABULARY_ROUTE))));
        //testAgainButton.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate(Constants.TEST_PATH)));

    }


}

