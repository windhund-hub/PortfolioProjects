package com.moc.vocabularywebapp.service.vocabulary;

import com.moc.vocabularywebapp.model.UserVocabularyList;
import com.moc.vocabularywebapp.model.UserVocabularyReference;
import com.moc.vocabularywebapp.model.Vocabulary;
import com.moc.vocabularywebapp.model.UserVocabulary;

import java.util.List;

//TODO TESt schreiben

public interface VocabularyService {

    //eventuell findall durch slices/pages oder streams ersetzem
    void save(Vocabulary vocabulary);
    List<Vocabulary> findAll();
    List<Vocabulary> findExpression(String substring);
    List<Vocabulary> findTranslation(String substring);
    void createVocabularyList(UserVocabularyList list);
    void setVocabularyList(UserVocabularyList list);
    UserVocabularyList getVocabularyList();
    void addVocabularyToUserList(UserVocabulary userVocabulary);
    void saveSelectedVocabularyToUserList(String useriD, UserVocabularyList userVocabularyList, Vocabulary vocabulary);
}
