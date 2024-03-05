package com.moc.vocabularywebapp.service.vocabulary;

import com.moc.vocabularywebapp.model.UserVocabularyList;
import com.moc.vocabularywebapp.model.Vocabulary;
import com.moc.vocabularywebapp.model.UserVocabulary;

import java.util.List;


public interface VocabularyService {

    void save(Vocabulary vocabulary);
    List<Vocabulary> findAll();
    List<Vocabulary> findExpression(String substring);
    List<Vocabulary> findTranslation(String substring);
    void createVocabularyList(UserVocabularyList list);
    void setVocabularyList(UserVocabularyList list);
    UserVocabularyList getVocabularyList();
    void addVocabularyToUserList(UserVocabulary userVocabulary);
    void saveSelectedVocabularyToUserList(String useriD, UserVocabularyList userVocabularyList, Vocabulary vocabulary);
    List<UserVocabularyList> getLists(String userId);
}
