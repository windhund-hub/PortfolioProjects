package com.moc.vocabularywebapp.service.vocabulary;

import com.moc.vocabularywebapp.model.Vocabulary;
import com.moc.vocabularywebapp.model.VocabularyList;

import java.util.List;

public interface VocabularyService {

    //eventuell findall durch slices/pages oder streams ersetzem
    void save(Vocabulary vocabulary);
    List<Vocabulary> findAll();
    List<Vocabulary> findExpression(String substring);
    List<Vocabulary> findTranslation(String substring);
    VocabularyList createVocabularyList(String userId, VocabularyList list);
}
