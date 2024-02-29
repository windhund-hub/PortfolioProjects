package com.moc.vocabularywebapp.service.vocabulary;

import com.moc.vocabularywebapp.model.UserVocabulary;
import com.moc.vocabularywebapp.model.UserVocabularyList;
import com.moc.vocabularywebapp.model.UserVocabularyReference;
import com.moc.vocabularywebapp.model.Vocabulary;
import com.moc.vocabularywebapp.repository.UserDBVocabularyRepository;
import com.moc.vocabularywebapp.repository.UserVocabularyListRepository;
import com.moc.vocabularywebapp.repository.UserVocabularyRepository;
import com.moc.vocabularywebapp.repository.VocabularyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VocabularyServiceImpl implements VocabularyService {

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private UserVocabularyListRepository listRepository;

    @Autowired
    private UserVocabularyRepository userVocabularyRepository;

    @Autowired
    private UserDBVocabularyRepository userDBVocabularyRepository;

    private UserVocabularyList currentList;

    private Vocabulary selectedVocabulary;

    @Override
    public void save(Vocabulary vocabulary) {
        vocabularyRepository.save(vocabulary);
    }

    @Override
    public List<Vocabulary> findAll() {
        return vocabularyRepository.findAll();
    }

    public List<Vocabulary> findExpression(String substring) {

        return vocabularyRepository.findExpression(substring);
    }
    public List<Vocabulary> findTranslation(String substring) {
        return vocabularyRepository.findTranslation(substring);
    }

    public void createVocabularyList(UserVocabularyList list) {
        listRepository.save(list);
    }

    public void setVocabularyList(UserVocabularyList list) {
         this.currentList = list;
    }

    public UserVocabularyList getVocabularyList() {
        return this.currentList;
    }

    public void addVocabularyToUserList(UserVocabulary userVocabulary) {
        userVocabularyRepository.save(userVocabulary);
    }

    @Override
    public void saveSelectedVocabularyToUserList(String useriD, UserVocabularyList userVocabularyList, Vocabulary vocabulary) {
        UserVocabularyReference userVocabularyReference = new UserVocabularyReference(useriD,userVocabularyList, vocabulary);

        userDBVocabularyRepository.save(userVocabularyReference);
    }


}
