package com.moc.vocabularywebapp.service.vocabulary;

import com.moc.vocabularywebapp.model.Vocabulary;
import com.moc.vocabularywebapp.model.VocabularyList;
import com.moc.vocabularywebapp.repository.VocabularyListRepository;
import com.moc.vocabularywebapp.repository.VocabularyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VocabularyServiceImpl implements VocabularyService {

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private VocabularyListRepository listRepository;

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

    public VocabularyList createVocabularyList(String userId, VocabularyList list) {
        list.setUserId(userId);
        return listRepository.save(list);
    }

}
