package com.moc.vocabularywebapp.presenter;

import com.moc.vocabularywebapp.service.vocabulary.VocabularyService;
import com.moc.vocabularywebapp.view.AllVocabularyView;

public class AllVocabularyPresenter {
    private final AllVocabularyView view;
    private final VocabularyService vocabularyService;

    public AllVocabularyPresenter(AllVocabularyView view, VocabularyService vocabularyService) {
        this.view = view;
        this.vocabularyService = vocabularyService;
        loadVocabulary();
    }

    public void loadVocabulary() {
        view.updateGrid(vocabularyService.findAll());
    }

    public void filterExpression(String filter) {
        view.updateGrid(vocabularyService.findExpression(filter));
    }

    public void filterTranslation(String filter) {
        view.updateGrid(vocabularyService.findTranslation(filter));
    }
}