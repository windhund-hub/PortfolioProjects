package com.moc.vocabularywebapp.presenter;

import com.moc.vocabularywebapp.constant.MessageKeys;
import com.moc.vocabularywebapp.constant.ResourceBundleNames;
import com.moc.vocabularywebapp.model.UserVocabulary;
import com.moc.vocabularywebapp.model.UserVocabularyList;
import com.moc.vocabularywebapp.model.Vocabulary;
import com.moc.vocabularywebapp.service.vocabulary.VocabularyService;
import com.moc.vocabularywebapp.view.AddVocabularyView;
import com.moc.vocabularywebapp.view.CreateVocabularyUserListView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.Locale;
import java.util.ResourceBundle;

//TODO
public class AddVocabularyPresenter {

    private Locale locale;
    private ResourceBundle message;
    private final AddVocabularyView view;
    private final VocabularyService vocabularyService;

    private UserVocabulary userVocabulary;


    public AddVocabularyPresenter(AddVocabularyView view, VocabularyService vocabularyService) {
        this.view = view;
        this.vocabularyService = vocabularyService;
        this.locale = UI.getCurrent().getLocale();
        this.message = ResourceBundle.getBundle(ResourceBundleNames.MESSAGE_BUNDLE, locale);

    }

    public void saveVocabulary() {
        userVocabulary = new UserVocabulary("123",vocabularyService.getVocabularyList(), view.getExpressionTF(),view.getTranslationTF());
        view.createBinder(userVocabulary);
        vocabularyService.addVocabularyToUserList(userVocabulary);
        view.clearFields();

    }


}
