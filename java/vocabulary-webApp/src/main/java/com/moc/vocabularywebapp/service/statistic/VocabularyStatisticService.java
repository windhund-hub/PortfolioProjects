package com.moc.vocabularywebapp.service.statistic;

import com.moc.vocabularywebapp.model.Vocabulary;
import com.moc.vocabularywebapp.model.VocabularyStatistic;

import java.util.List;

public interface VocabularyStatisticService {

    void save(VocabularyStatistic vocabularyStatistic);
    VocabularyStatistic findById(Integer id);
    VocabularyStatistic updateStatistic(Integer id, int numberOfTrainings, int numberOfSuccess);


}
