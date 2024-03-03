package com.moc.vocabularywebapp.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.StreamResource;

public class FlagsLayout extends HorizontalLayout {

    private Image flagGerman;
    private Image flagAmerican;

// <a href="https://www.flaticon.com/de/kostenlose-icons/deutschland" title="deutschland Icons">Deutschland Icons erstellt von Freepik - Flaticon</a>
// <a href="https://www.flaticon.com/de/kostenlose-icons/vereinigte-staaten" title="vereinigte staaten Icons">Vereinigte staaten Icons erstellt von Freepik - Flaticon</a>

    public Image getGermanFlag(){
        StreamResource imageResource = new StreamResource("deutschland.png",
                () -> getClass().getResourceAsStream("/images/deutschland.png"));

        Image image = new Image(imageResource, "My Image");
        image.setMaxHeight("50px");

        return image;
    }

    public Image getAmericanFlag(){
        StreamResource imageResource = new StreamResource("vereinigte-staaten.png",
                () -> getClass().getResourceAsStream("/images/vereinigte-staaten.png"));

        Image image = new Image(imageResource, "My Image");

        image.setMaxHeight("50px");
        return image;
    }

}
