package com.moc.vocabularywebapp.repository;

import com.moc.vocabularywebapp.model.VocabularyList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabularyListRepository extends MongoRepository<VocabularyList, String> {
    List<VocabularyList> findByUserId(String userId);
}

