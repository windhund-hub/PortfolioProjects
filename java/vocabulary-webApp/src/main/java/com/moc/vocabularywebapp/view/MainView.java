package com.moc.vocabularywebapp.view;

import com.moc.vocabularywebapp.constant.LabelKeys;
import com.moc.vocabularywebapp.constant.ResourceBundleNames;
import com.moc.vocabularywebapp.constant.RouteKeys;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Locale;
import java.util.ResourceBundle;

//TODO _> LAnguage, eigene VoakbelListe mit eignen Vokabelen
@PageTitle(value = "Home")
@Route(value = "")
public class MainView  extends AppLayout{
    private Locale locale;
    private ResourceBundle label;
    private ResourceBundle route;

    public MainView(){
        createVariables();
        DrawerToggle toggle = new DrawerToggle();
        H1 title = new H1(label.getString(LabelKeys.APP_TITLE));
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        SideNav nav = getSideNav();

        Scroller scroller = new Scroller(nav);
        scroller.setClassName(LumoUtility.Padding.SMALL);
        addToDrawer(scroller);
        addToNavbar(toggle, title);

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

                //TODO  aktion switch language einfügem
                //new SideNavItem(label.getString(LabelKeys.SITE_LANGUAGE_SIDENAV), route.getString(RouteKeys.SITE_LANGUAGE_ROUTE),
                //VaadinIcon.FLAG_O.create()));
                       // VaadinIcon.SMILEY_O.create()));
                 SideNavItem siteLanguage = new SideNavItem(label.getString(LabelKeys.SITE_LANGUAGE_SIDENAV));
                 siteLanguage.setPrefixComponent(VaadinIcon.FLAG_O.create());
                 siteLanguage.addItem(new SideNavItem(label.getString(LabelKeys.GERMAN_SIDENAV)));
                 siteLanguage.addItem(new SideNavItem(label.getString(LabelKeys.ENGLISH_SIDENAV)));
                 sideNav.addItem(siteLanguage);




        return sideNav;
    }



    /*private Checkbox themeToggle;
    private static boolean isChecked;


    private Checkbox createToogle() {
        themeToggle = new Checkbox("Dark Mode");
        themeToggle.setValue(isChecked);
        themeToggle.addValueChangeListener(event -> {
            MainView.isChecked = !isChecked;
            setTheme(isChecked);
        });
        return themeToggle;
    }
    private void setTheme(boolean dark) {

        var js = "document.documentElement.setAttribute('theme', $0)";
        getElement().executeJs(js,dark ? Lumo.DARK : Lumo.LIGHT );
    }*/

}
