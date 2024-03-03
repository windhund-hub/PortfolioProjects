package com.moc.vocabularywebapp.view;

import com.moc.vocabularywebapp.constant.LabelKeys;
import com.moc.vocabularywebapp.constant.ResourceBundleNames;
import com.moc.vocabularywebapp.constant.RouteKeys;
import com.moc.vocabularywebapp.model.UserVocabularyList;
import com.moc.vocabularywebapp.service.vocabulary.VocabularyService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.BeanWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

//TODO _> LAnguage, eigene VoakbelListe mit eignen Vokabelen
@PageTitle(value = "Home")
@Route(value = "")
public class MainView  extends AppLayout{
    private Locale locale;
    private ResourceBundle label;
    private ResourceBundle route;
    private Button themeToggle;
    private VocabularyService vocabularyService;

    public MainView(VocabularyService vocabularyService){
        this.vocabularyService = vocabularyService;
        createVariables();
        DrawerToggle toggle = new DrawerToggle();
        H1 title = new H1(label.getString(LabelKeys.APP_TITLE));

        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        title.setWidthFull();
        SideNav nav = getSideNav();

        Scroller scroller = new Scroller(nav);
        scroller.setClassName(LumoUtility.Padding.SMALL);
        addToDrawer(scroller);
        HorizontalLayout languageSwitcher = createLanguageSwitcher();
        languageSwitcher.getElement();
        HorizontalLayout toogleTheme = new HorizontalLayout();
        toogleTheme.add(createToogle());

        addToNavbar(toggle, title, languageSwitcher, toogleTheme);

    }

    private void createVariables(){
        locale = UI.getCurrent().getLocale();
        label = ResourceBundle.getBundle(ResourceBundleNames.LABLE_BUNDLE, locale);
        route = ResourceBundle.getBundle(ResourceBundleNames.ROUTE_BUNDLE, locale);
    }

    private SideNav getSideNav() {

        SideNav sideNav = new SideNav();

        sideNav.addItem(
                new SideNavItem(label.getString(LabelKeys.ALL_VOCABULARY_SIDENAV), route.getString(RouteKeys.ALL_VOCABULARY_ROUTE),
                        VaadinIcon.GRID_SMALL.create()),
                new SideNavItem(label.getString(LabelKeys.STUDY_SIDENAV), route.getString(RouteKeys.STUDY_VOCABULARY_ROUTE), VaadinIcon.OPEN_BOOK.create()),
                new SideNavItem(label.getString(LabelKeys.TEST_SIDENAV), route.getString(RouteKeys.TEST_VOCABULARY_ROUTE),
                        VaadinIcon.ACADEMY_CAP.create()),
                //TODO eigene liste mit eigenen vokbeln und auswahl aus verhandnen
                new SideNavItem(label.getString(LabelKeys.CREATE_VOCABULARYLIST_SIDENAV), route.getString(RouteKeys.CREATE_VOCABULARY_LIST_ROUTE),
                        VaadinIcon.HAMMER.create()));

                //Vokabellisten nach login
                SideNavItem userLists = new SideNavItem(label.getString(LabelKeys.USER_LIST_SIDENAV));
                userLists.setPrefixComponent(VaadinIcon.FLAG_O.create());
                List<UserVocabularyList> list = vocabularyService.getLists("123");
                for(UserVocabularyList name: list){
                    userLists.addItem(new SideNavItem(name.getListName()));
                }


                sideNav.addItem(userLists);

                //TODO  aktion switch language einfügem
                //new SideNavItem(label.getString(LabelKeys.SITE_LANGUAGE_SIDENAV), route.getString(RouteKeys.SITE_LANGUAGE_ROUTE),
                //VaadinIcon.FLAG_O.create()));
                       // VaadinIcon.SMILEY_O.create()));
                /* SideNavItem siteLanguage = new SideNavItem(label.getString(LabelKeys.SITE_LANGUAGE_SIDENAV));
                 siteLanguage.setPrefixComponent(VaadinIcon.FLAG_O.create());
                 siteLanguage.addItem(new SideNavItem(label.getString(LabelKeys.GERMAN_SIDENAV)));
                 siteLanguage.addItem(new SideNavItem(label.getString(LabelKeys.ENGLISH_SIDENAV)));
                 sideNav.addItem(siteLanguage);*/



        return sideNav;
    }


    private HorizontalLayout createLanguageSwitcher() {
        FlagsLayout flags = new FlagsLayout();

        Button germanFlagButton = new Button(flags.getGermanFlag(), click -> switchLanguage(Locale.GERMAN));
        germanFlagButton.getStyle().set("background-color", "transparent").set("border", "none").set("cursor", "pointer");

        Button americanFlagButton = new Button(flags.getAmericanFlag(), click -> switchLanguage(Locale.ENGLISH));
        americanFlagButton.getStyle().set("background-color", "transparent").set("border", "none").set("cursor", "pointer");

        HorizontalLayout languageSwitcher = new HorizontalLayout(germanFlagButton, americanFlagButton);
        languageSwitcher.setWidthFull();
        languageSwitcher.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        return languageSwitcher;
    }

    private void switchLanguage(Locale newLocale) {
        UI.getCurrent().setLocale(newLocale);
        UI.getCurrent().getPage().reload();
    }

    private Button createToogle() {
        themeToggle = new Button("Switch to Dark Mode");
        //themeToggle.setValue(isChecked);
        themeToggle.addClickListener(event -> {
            if(themeToggle.getText().equals("Switch to Dark Mode")){
                setTheme(true);
                themeToggle.setText("Switch to Light Mode");
            }else{
                setTheme(false);
                themeToggle.setText("Switch to Dark Mode");
            }
        });
        return themeToggle;
    }
    private void setTheme(boolean dark) {

        var js = "document.documentElement.setAttribute('theme', $0)";
        getElement().executeJs(js,dark ? Lumo.DARK : Lumo.LIGHT );
    }

}
