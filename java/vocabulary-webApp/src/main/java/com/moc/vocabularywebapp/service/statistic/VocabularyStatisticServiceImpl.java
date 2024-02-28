package com.moc.vocabularywebapp.service.statistic;

import com.moc.vocabularywebapp.model.Vocabulary;
import com.moc.vocabularywebapp.model.VocabularyStatistic;
import com.moc.vocabularywebapp.repository.VocabularyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//TODO
@Service
public class VocabularyStatisticServiceImpl implements VocabularyStatisticService {

    @Autowired
    private VocabularyRepository vocabularyRepository;


    @Override
    public VocabularyStatistic updateStatistic(String vocabularyId, int numberOfTrainings, int numberOfSuccess) {
        Vocabulary vocabulary = vocabularyRepository.findById(vocabularyId)
                .orElseThrow(() -> new RuntimeException("Vokabel nicht gefunden!"));

        vocabulary.getVocabularyStatistic().setNumberOfTraining(numberOfTrainings);
        vocabulary.getVocabularyStatistic().setNumberOfSuccess(numberOfSuccess);

        vocabularyRepository.save(vocabulary);

        return vocabulary.getVocabularyStatistic();
    }

}
