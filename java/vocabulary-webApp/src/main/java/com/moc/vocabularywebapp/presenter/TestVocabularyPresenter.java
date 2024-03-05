package com.moc.vocabularywebapp.presenter;

import com.moc.vocabularywebapp.model.Vocabulary;
import com.moc.vocabularywebapp.service.result.ResultService;
import com.moc.vocabularywebapp.service.statistic.VocabularyStatisticService;
import com.moc.vocabularywebapp.service.vocabulary.VocabularyService;
import com.moc.vocabularywebapp.view.TestVocabularyView;
import java.util.List;
import java.util.ListIterator;

public class TestVocabularyPresenter {

    private final TestVocabularyView view;
    private final VocabularyService vocabularyService;
    private final ResultService resultService;
    private final VocabularyStatisticService vocabularyStatisticService;
    private ListIterator<Vocabulary> iter;
    private Vocabulary currentVocabulary;

    public TestVocabularyPresenter(TestVocabularyView view, VocabularyService vocabularyService, ResultService resultService, VocabularyStatisticService vocabularyStatisticService) {
        this.view = view;
        this.vocabularyService = vocabularyService;
        this.resultService = resultService;
        this.vocabularyStatisticService = vocabularyStatisticService;
        loadVocabularies();
    }

    private void loadVocabularies() {
        List<Vocabulary> vocabularyList = vocabularyService.findAll();
        if (!vocabularyList.isEmpty()) {
            iter = vocabularyList.listIterator();
            showNextVocabulary();
        } else {
            view.showNoVocabularyFound();
        }
    }

    public void testVocabulary(String userTranslation) {

        boolean isCorrect = userTranslation.trim().equalsIgnoreCase(currentVocabulary.getTranslation());
        if (isCorrect) {
            resultService.setResult(currentVocabulary,"true");
            view.showCorrectAnswerNotification();
            updateStats(true);
            showNextVocabulary();
        } else {
            resultService.setResult(currentVocabulary, "false");
            view.showWrongAnswerNotification();
            view.clearTranslationField();
        }
    }
    public void showNextVocabulary() {
        if (iter.hasNext()) {
            currentVocabulary = iter.next();
            view.setExpression(currentVocabulary.getExpression());
            view.clearTranslationField();
            view.updateButtonsForNextVocabulary();
        } else {
            view.showTestFinished();
        }
    }

    public void showSolution() {

        view.setTranslation(currentVocabulary.getTranslation());
        if(iter.hasNext()){
            view.updateButtonsForSolution(false);
        }else{
            view.updateButtonsForSolution(true);
        }
        resultService.setResult(currentVocabulary, "false");
        updateStats(false);
    }

    public void getHelp() {
        view.clearTranslationField();
        String currentTranslation = currentVocabulary.getTranslation();
        int wordLength = currentTranslation.length();
        String clue;
        if (wordLength > 3) {
            int numberOfDots = wordLength - 2;
            String dots = view.buildHelp().repeat(numberOfDots);
            clue = currentTranslation.charAt(0) + dots +  currentTranslation.charAt(wordLength - 1);
        } else {
            int numberOfDots = wordLength - 1;
            String dots = view.buildHelp().repeat(numberOfDots);
            clue = currentTranslation.charAt(0) + dots;
        }
        view.showHelp(clue);
    }

    private void updateStats(boolean success) {
        // Logik zur Aktualisierung der Statistiken
        String vocabularyId = currentVocabulary.getId();
        int numberOfTrainings = currentVocabulary.getVocabularyStatistic().getNumberOfTraining();
        int numberOfSuccess = currentVocabulary.getVocabularyStatistic().getNumberOfSuccess();
        if (success) {
            numberOfSuccess += 1;
        }
        numberOfTrainings += 1;
        vocabularyStatisticService.updateStatistic(vocabularyId, numberOfTrainings, numberOfSuccess);
        //exitiert id überhaupt -> prüfen
    }

}
