package com.moc.vocabularywebapp.model;

import com.moc.vocabularywebapp.repository.VocabularyRepository;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class VocabularyEntityTest {

    @Autowired
    private VocabularyRepository vocabularyRepository;

    private Vocabulary vocabulary;

    private Validator validator;


    @BeforeEach
    void setUp() {
        //vocabularyRepositoryMongo.deleteAll();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        // Arrange
        vocabulary = new Vocabulary("Hase", "rabbit");
    }
    @Test
    void testVocabulary_whenValidVocabularyProvided_thenNoConstraintViolations() {
        // Act
        Set<ConstraintViolation<Vocabulary>> violations = validator.validate(vocabulary);

        // Assert
        assertTrue(violations.isEmpty(), "Expected no constraint violations");
    }

    @Test
    void testVocabulary_whenInvalidVocabularyIsProvided_thenConstraintViolation() {
       vocabulary = new Vocabulary("123", "d0g"); // Invalid expression

        Set<ConstraintViolation<Vocabulary>> violations = validator.validate(vocabulary);

        assertFalse(violations.isEmpty(), "Expected a constraint violation due to invalid vocabulary");
    }

    @Test
    void testVocabularyRelation_whenSetVocabulary_thenCorrectStatisticDefault() {
        vocabularyRepository.save(vocabulary);

        assertEquals(0, vocabulary.getVocabularyStatistic().getNumberOfTraining(), "Expected value should be 0");
        assertEquals(0, vocabulary.getVocabularyStatistic().getNumberOfSuccess(), "Expected value should be 0");
    }

}