package com.moc.vocabularywebapp.service.statistic;

import com.moc.vocabularywebapp.model.Vocabulary;
import com.moc.vocabularywebapp.repository.VocabularyStatisticsRepository;
import com.moc.vocabularywebapp.model.VocabularyStatistic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VocabularyStatisticServiceImpl implements VocabularyStatisticService{

    @Autowired
    private VocabularyStatisticsRepository vocabularyStatisticsRepository;
    @Override
    public void save(VocabularyStatistic vocabularyStatistic) {
        vocabularyStatisticsRepository.save(vocabularyStatistic);
    }

    public VocabularyStatistic findById(Integer id) {
        Optional<VocabularyStatistic> result = vocabularyStatisticsRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        } else {
            throw new RuntimeException("Statistik mit ID " + id + " nicht gefunden.");
        }
    }

    @Override
    public VocabularyStatistic updateStatistic(Integer id, int numberOfTrainings, int numberOfSuccess) {
        return vocabularyStatisticsRepository.findById(id).map(vocabularyStatistic -> {
            // Aktualisiere die Eigenschaften des Eintrags

            vocabularyStatistic.setNumberOfTraining(numberOfTrainings);
            vocabularyStatistic.setNumberOfSuccess(numberOfSuccess);
            // Speichere den aktualisierten Eintrag
            return vocabularyStatisticsRepository.save(vocabularyStatistic);
        }).orElseThrow(() -> new RuntimeException("Statistik nicht gefunden!"));
    }
}
