package com.moc.vocabularywebapp.service.statistic;

import com.moc.vocabularywebapp.model.VocabularyStatistic;

public interface VocabularyStatisticService {

    void save(VocabularyStatistic vocabularyStatistic, String vocabularyId);
    VocabularyStatistic updateStatistic(String vocabularyId, int numberOfTrainings, int numberOfSuccess);
}
