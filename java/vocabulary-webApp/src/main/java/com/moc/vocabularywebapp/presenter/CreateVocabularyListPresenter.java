package com.moc.vocabularywebapp.presenter;

import com.moc.vocabularywebapp.constant.ResourceBundleNames;
import com.moc.vocabularywebapp.model.UserVocabulary;
import com.moc.vocabularywebapp.model.UserVocabularyList;
import com.moc.vocabularywebapp.service.vocabulary.VocabularyService;
import com.moc.vocabularywebapp.view.CreateVocabularyUserListView;
import com.vaadin.flow.component.UI;

import java.util.Locale;
import java.util.ResourceBundle;

public class CreateVocabularyListPresenter {

    private Locale locale;
    private ResourceBundle message;
    private final CreateVocabularyUserListView view;
    private final VocabularyService vocabularyService;

    private UserVocabularyList userVocabularyList;


    public CreateVocabularyListPresenter(CreateVocabularyUserListView view, VocabularyService vocabularyService) {
        this.view = view;
        this.vocabularyService = vocabularyService;
        this.locale = UI.getCurrent().getLocale();
        this.message = ResourceBundle.getBundle(ResourceBundleNames.MESSAGE_BUNDLE, locale);
    }

    public void saveList() {
        userVocabularyList = new UserVocabularyList("123",
                view.setListNameFromTF());
        view.createBinder(userVocabularyList);
        vocabularyService.createVocabularyList(userVocabularyList);

        view.showListCreated();
        view.setButtonsForAddVocabulary();
    }
}
