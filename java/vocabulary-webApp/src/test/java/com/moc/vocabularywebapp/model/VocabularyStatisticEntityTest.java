package com.moc.vocabularywebapp.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class VocabularyStatisticEntityTest {

    private VocabularyStatistic vocabularyStatistic;
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        // Arrange
        vocabularyStatistic = new VocabularyStatistic();
    }

    @Test
    void testVocabularyStatistic_whenVocabularyCreated_thenNoConstraintViolations() {
        // Act
        Set<ConstraintViolation<VocabularyStatistic>> violations = validator.validate(vocabularyStatistic);

        // Assert
        assertTrue(violations.isEmpty(), "Expected no constraint violations");
    }

    @Test
    void testGetSuccessRate_successDivideByTraining_returnsCorrectCalculation() {

        vocabularyStatistic.setNumberOfTraining(6);
        vocabularyStatistic.setNumberOfSuccess(3);
        assertEquals(0.5, vocabularyStatistic.getSuccessRate());

        vocabularyStatistic.setNumberOfTraining(0);
        vocabularyStatistic.setNumberOfSuccess(3);
        assertEquals(0.0, vocabularyStatistic.getSuccessRate());
    }
}