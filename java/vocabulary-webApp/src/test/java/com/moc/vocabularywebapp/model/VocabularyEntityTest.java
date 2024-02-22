package com.moc.vocabularywebapp.model;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class VocabularyEntityTest {


    private Vocabulary vocabulary;

    private Validator validator;

    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        // Arrange
        vocabulary = new Vocabulary("Hund", "dog");
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
        vocabulary = testEntityManager.persistAndFlush(vocabulary);

        assertEquals(0, vocabulary.getVocabularyStatistic().getNumberOfTraining(), "Expected value should be 0");
        assertEquals(0, vocabulary.getVocabularyStatistic().getNumberOfSuccess(), "Expected value should be 0");
    }

}