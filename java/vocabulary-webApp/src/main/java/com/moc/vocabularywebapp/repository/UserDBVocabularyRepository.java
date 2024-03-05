package com.moc.vocabularywebapp.repository;

import com.moc.vocabularywebapp.model.UserVocabularyReference;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserDBVocabularyRepository extends MongoRepository<UserVocabularyReference, String> {


}
