package com.moc.vocabularywebapp.repository;

import com.moc.vocabularywebapp.model.Vocabulary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.springframework.test.context.ActiveProfiles;

@DataMongoTest
@ActiveProfiles("test")
class VocabularyRepositoryTest {


    @Autowired
    private VocabularyRepository vocabularyRepository;

    @BeforeEach
    void setUp() {
        //Arrange
        vocabularyRepository.deleteAll();

        Vocabulary vocabulary = new Vocabulary("Hund", "dog");
        Vocabulary vocabulary2 = new Vocabulary("Elefant", "elephant");
        Vocabulary vocabulary3 = new Vocabulary("Adler", "eagle");
        vocabularyRepository.save(vocabulary);
        vocabularyRepository.save(vocabulary2);
        vocabularyRepository.save(vocabulary3);
    }

    //@Disabled
    @Test
    void testFindExpression_whenGivenSubstring_returnsCorrectExpressions() {


        // Act
        List<Vocabulary> storedVocabulary = vocabularyRepository.findExpression("n");

        // Assert
        assertThat(storedVocabulary).hasSize(2).extracting(Vocabulary::getExpression)
                .containsOnly("Hund", "Elefant");
    }

    //@Disabled
    @Test
    void testFindTranslation_whenGivenSubstring_returnsCorrectTranslation() {

        // Act
        List<Vocabulary> storedVocabulary = vocabularyRepository.findTranslation("n");

        // Assert
        assertThat(storedVocabulary).hasSize(1).extracting(Vocabulary::getTranslation)
                .containsOnly("elephant");

    }

    /*@Test
    void testFindIdByExpression_whenGivenExpression_returnsCorrectId() {
        VocabularyMongo v = new VocabularyMongo("Hase", "rabbit");
        //vocabularyRepository.save(v);
        testEntityManager.persistAndFlush(v);
        VocabularyMongo storedVocabulary = vocabularyRepository.findIdByExpression(v.getExpression());

        // Assert
        Assertions.assertEquals(10, storedVocabulary.getId(),
                "Id should be 10");



    }*/
}