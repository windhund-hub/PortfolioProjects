package com.moc.vocabularywebapp.presenter;

import com.moc.vocabularywebapp.model.Vocabulary;
import com.moc.vocabularywebapp.service.result.ResultService;
import com.moc.vocabularywebapp.service.statistic.VocabularyStatisticService;
import com.moc.vocabularywebapp.service.vocabulary.VocabularyService;
import com.moc.vocabularywebapp.view.TestVocabularyView;
import java.util.List;
import java.util.ListIterator;
import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.FALSE;


//TODO Change String wrong answer
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

    public void showSolution() {
        view.setTranslation(currentVocabulary.getTranslation());
        view.updateButtonsForSolution();
        resultService.setResult(currentVocabulary, "false");
        updateStats(false);
    }

    public void getHelp() {
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
        int foreignKey = currentVocabulary.getVocabularyStatistic().getId();
        int numberOfTrainings = vocabularyStatisticService.findById(foreignKey).getNumberOfTraining();
        int numberOfSuccess = vocabularyStatisticService.findById(foreignKey).getNumberOfSuccess();
        if (success) {
            numberOfSuccess += 1;
        }
        numberOfTrainings += 1;
        vocabularyStatisticService.updateStatistic(foreignKey, numberOfTrainings, numberOfSuccess);
        //exitiert id überhaupt -> prüfen
    }

}
