package com.moc.vocabularywebapp.presenter;

import com.moc.vocabularywebapp.model.UserVocabularyList;
import com.moc.vocabularywebapp.model.Vocabulary;
import com.moc.vocabularywebapp.service.vocabulary.VocabularyService;
import com.moc.vocabularywebapp.view.AddDBVocabularyView;
import java.util.Set;

//TODO nach speichern der vokabeln, neu laden und schon gespeicherten ausgrauen
public class AddDBVocabularyPresenter {


    private final AddDBVocabularyView view;
    private final VocabularyService vocabularyService;
    private Vocabulary vocabulary;
    private Set<Vocabulary> selectedVocabularies;


    public AddDBVocabularyPresenter(AddDBVocabularyView view, VocabularyService vocabularyService) {
        this.view = view;
        this.vocabularyService = vocabularyService;
        loadVocabulary();
    }

    public void loadVocabulary() {
        view.updateGrid(vocabularyService.findAll());
    }


    public void saveSelectedVocabularies(String userId, UserVocabularyList list) {
        selectedVocabularies.forEach(vocabulary ->
                vocabularyService.saveSelectedVocabularyToUserList(userId, list, vocabulary));
        view.showNotification();
        loadVocabulary();
    }

    public void selectionChanged(Set<Vocabulary> selectedVocabularies) {
        this.selectedVocabularies = selectedVocabularies;
    }

}
