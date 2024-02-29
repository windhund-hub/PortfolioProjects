package com.moc.vocabularywebapp.repository;

import com.moc.vocabularywebapp.model.UserVocabulary;
import com.moc.vocabularywebapp.model.Vocabulary;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserVocabularyRepository  extends MongoRepository<UserVocabulary, String> {
}
