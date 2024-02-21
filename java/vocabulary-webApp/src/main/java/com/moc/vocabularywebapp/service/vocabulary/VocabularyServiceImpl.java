package com.moc.vocabularywebapp.service.vocabulary;

import com.moc.vocabularywebapp.model.Vocabulary;
import com.moc.vocabularywebapp.repository.VocabularyRepository;
import com.moc.vocabularywebapp.model.VocabularyStatistic;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//TODO findStatisticId
@Service
public class VocabularyServiceImpl implements VocabularyService{

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Override
    public void save(Vocabulary vocabulary) {
        vocabularyRepository.save(vocabulary);
    }

    @Override
    public List<Vocabulary> findAll() {
        return vocabularyRepository.findAll();
    }

    @Override
    public List<Vocabulary> findExpression(String substring) {

        return vocabularyRepository.findExpression(substring);
    }
    @Override
    public List<Vocabulary> findTranslation(String substring) {
        return vocabularyRepository.findTranslation(substring);
    }

    public VocabularyStatistic findStatisticId(Integer id){
        return vocabularyRepository.findById(id).map(Vocabulary::getVocabularyStatistic).orElseThrow(() -> new EntityNotFoundException("blaaaaa"));
    }

    @Override
    public Integer findIdByExpression(String expression) {
        //return vocabularyRepository.findIdByName(expression);
        return vocabularyRepository.findIdByExpression(expression);
    }

}
