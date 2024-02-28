package com.moc.vocabularywebapp.repository;

import com.moc.vocabularywebapp.model.UserVocabulary;
import com.moc.vocabularywebapp.model.UserVocabularyList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

//TODO TESt schreiben
@Repository
public interface UserVocabularyListRepository extends MongoRepository<UserVocabulary, String> {

    void save(UserVocabularyList list);

}

