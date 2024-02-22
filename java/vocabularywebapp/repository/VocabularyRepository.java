package com.moc.vocabularywebapp.repository;

import com.moc.vocabularywebapp.model.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Integer> {

    @Query("select v from Vocabulary v where lower(v.expression) like lower(concat('%',:substring,'%' ))")
    List<Vocabulary> findExpression(String substring);

    @Query("select v from Vocabulary v where lower(v.translation) like lower(concat('%',:substring,'%' ))")
    List<Vocabulary> findTranslation(String substring);

    Vocabulary findIdByExpression(String expression);






}
