package com.moc.vocabularywebapp.presenter;

import com.moc.vocabularywebapp.model.Vocabulary;
import com.moc.vocabularywebapp.service.vocabulary.VocabularyService;
import com.moc.vocabularywebapp.view.StudyVocabularyView;
import java.util.List;
import java.util.ListIterator;

public class StudyVocabularyPresenter {
    private StudyVocabularyView view;
    private final VocabularyService vocabularyService;
    private List<Vocabulary> vocabularyList;
    private Vocabulary currentVocabulary;
    private ListIterator<Vocabulary> iter;
    private boolean next;
    private boolean previous;

    public StudyVocabularyPresenter(VocabularyService vocabularyService, StudyVocabularyView view) {
        this.vocabularyService = vocabularyService;
        this.view = view;
        previous = false;
        loadVocabularies();
    }

    private void loadVocabularies() {
        vocabularyList = vocabularyService.findAll();
        iter = vocabularyList.listIterator();
        currentVocabulary = iter.next();
        if(iter.hasNext()) {
            updateViewWithCurrentVocabulary(currentVocabulary);
        }
    }

    private void updateViewWithCurrentVocabulary(Vocabulary currentVocabulary) {
        view.setExpression(currentVocabulary.getExpression());
        view.setTranslation(currentVocabulary.getTranslation());
    }

    public void showNextVocabulary() {
        navigateVocabulary(NavigationDirection.NEXT);
    }

    public void showPreviousVocabulary() {
        navigateVocabulary(NavigationDirection.PREVIOUS);
    }
    private void navigateVocabulary(NavigationDirection direction) {
        if (direction == NavigationDirection.NEXT && iter.hasNext()) {

            next = true;
            if (previous) {
                iter.next();
                previous = false;
            }
            currentVocabulary = iter.next();
            updateViewWithCurrentVocabulary(currentVocabulary);
        } else if (direction == NavigationDirection.PREVIOUS && iter.hasPrevious()) {

            previous = true;
            if(next){
                iter.previous();
                next = false;
            }
            currentVocabulary = iter.previous();
            updateViewWithCurrentVocabulary(currentVocabulary);
        }
        updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        view.setBackButtonVisibility(iter.hasPrevious());
        view.setNextButtonVisibility(iter.hasNext());
    }
}
