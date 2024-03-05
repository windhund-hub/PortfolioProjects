package com.moc.vocabularywebapp.presenter;

import com.moc.vocabularywebapp.service.result.ResultService;
import com.moc.vocabularywebapp.view.TestResultView;

public class TestResultPresenter {
    private TestResultView view;
    private ResultService resultService;

    public TestResultPresenter(TestResultView view, ResultService resultService) {
        this.view = view;
        this.resultService = resultService;
        loadResults();
    }

    private void loadResults() {
        view.updateGrid(resultService.getResults().entrySet());
    }
}
