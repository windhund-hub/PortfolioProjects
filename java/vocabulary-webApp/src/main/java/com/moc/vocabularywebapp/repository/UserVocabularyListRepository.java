package com.moc.vocabularywebapp.repository;

import com.moc.vocabularywebapp.model.UserVocabulary;
import com.moc.vocabularywebapp.model.UserVocabularyList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVocabularyListRepository extends MongoRepository<UserVocabularyList, String> {


}

