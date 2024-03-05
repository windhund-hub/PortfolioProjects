package com.moc.vocabularywebapp.presenter;

import com.moc.vocabularywebapp.constant.ResourceBundleNames;
import com.moc.vocabularywebapp.model.UserVocabulary;
import com.moc.vocabularywebapp.service.vocabulary.VocabularyService;
import com.moc.vocabularywebapp.view.AddVocabularyView;
import com.vaadin.flow.component.UI;
import java.util.Locale;
import java.util.ResourceBundle;

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
