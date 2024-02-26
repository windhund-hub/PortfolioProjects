package com.moc.vocabularywebapp.repository;

import com.moc.vocabularywebapp.model.Vocabulary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabularyRepository extends MongoRepository<Vocabulary, String> {

    @Query("{ 'expression': { $regex: ?0, $options: 'i' } }")
    List<Vocabulary> findExpression(String expression);

    @Query("{ 'translation': { $regex: ?0, $options: 'i' } }")
    List<Vocabulary> findTranslation(String translation);

}
