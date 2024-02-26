package com.moc.vocabularywebapp.view;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.StreamResource;

public class FlagsLayout extends HorizontalLayout {

    private Image flagGerman;
    private Image flagAmerican;

// <a href="https://www.flaticon.com/de/kostenlose-icons/deutschland" title="deutschland Icons">Deutschland Icons erstellt von Freepik - Flaticon</a>
// <a href="https://www.flaticon.com/de/kostenlose-icons/vereinigte-staaten" title="vereinigte staaten Icons">Vereinigte staaten Icons erstellt von Freepik - Flaticon</a>
    public FlagsLayout(){

    }

    public Image getGermanFlag(){
        Image image = new Image("images/deutschland.png", "My Image");
        setJustifyContentMode(JustifyContentMode.CENTER);
        return image;
    }

    public Image getAmericanFlag(){
        Image image = new Image("images/vereinigte-staaten.png", "My Image");
        setJustifyContentMode(JustifyContentMode.CENTER);
        return image;
    }

}
