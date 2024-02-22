package com.moc.vocabularywebapp.service.vocabulary;

import com.moc.vocabularywebapp.model.Vocabulary;
import com.moc.vocabularywebapp.model.VocabularyStatistic;

import java.util.List;

public interface VocabularyService {

    //eventuell findall durch slices/pages oder streams ersetzem
    void save(Vocabulary vocabulary);
    List<Vocabulary> findAll();
    List<Vocabulary> findExpression(String substring);
    List<Vocabulary> findTranslation(String substring);
    VocabularyStatistic findStatisticId(Integer id);
    Vocabulary findIdByExpression(String expression);
    //Integer findIdByExpression(String expression);


}
