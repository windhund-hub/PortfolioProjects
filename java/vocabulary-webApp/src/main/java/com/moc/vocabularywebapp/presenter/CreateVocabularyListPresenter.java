package com.moc.vocabularywebapp.presenter;

import com.moc.vocabularywebapp.constant.MessageKeys;
import com.moc.vocabularywebapp.constant.ResourceBundleNames;
import com.moc.vocabularywebapp.model.VocabularyList;
import com.moc.vocabularywebapp.service.vocabulary.VocabularyService;
import com.moc.vocabularywebapp.view.CreateVocabularyUserListView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import java.util.Locale;
import java.util.ResourceBundle;

public class CreateVocabularyListPresenter {

    private Locale locale;
    private ResourceBundle message;

    private final CreateVocabularyUserListView view;
    private final VocabularyService vocabularyService;


    public CreateVocabularyListPresenter(CreateVocabularyUserListView view, VocabularyService vocabularyService) {
        this.view = view;
        this.vocabularyService = vocabularyService;
        this.locale = UI.getCurrent().getLocale();
        this.message = ResourceBundle.getBundle(ResourceBundleNames.MESSAGE_BUNDLE, locale);
    }

    public void saveList() {
        VocabularyList list = new VocabularyList();
        vocabularyService.createVocabularyList("userId123", list);

        Notification listCreated = Notification.
                show(message.getString(MessageKeys.LIST_CREATED));
        listCreated.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        listCreated.setPosition(Notification.Position.TOP_START);
        view.setButtonsForAddVocabulary();
    }
}
