package com.moc.vocabularywebapp.presenter;

import com.moc.vocabularywebapp.model.UserVocabularyList;
import com.moc.vocabularywebapp.service.vocabulary.VocabularyService;
import com.moc.vocabularywebapp.view.CreateVocabularyUserListView;


public class CreateVocabularyListPresenter {

    private final CreateVocabularyUserListView view;
    private final VocabularyService vocabularyService;
    private UserVocabularyList userVocabularyList;


    public CreateVocabularyListPresenter(CreateVocabularyUserListView view, VocabularyService vocabularyService) {
        this.view = view;
        this.vocabularyService = vocabularyService;
    }

    public void saveList() {
        userVocabularyList = new UserVocabularyList("123",
                view.setListNameFromTF());
        view.createBinder(userVocabularyList);
        vocabularyService.createVocabularyList(userVocabularyList);
        vocabularyService.setVocabularyList(userVocabularyList);
        view.showListCreated();
        view.setButtonsForAddVocabulary();
    }
}
