package com.moc.vocabularywebapp.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vocabularies") // Markiert die Klasse als ein Dokument, das in der MongoDB gespeichert wird
@TypeAlias("Vocabulary")
public class Vocabulary {

    @Id
    private String id;

    @NotEmpty(message = "Expression muss gesetzt werden.")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Es dürfen nur Buchstaben enthalten sein")
    private String expression;

    @NotEmpty(message = "Übersetzung muss gesetzt werden.")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Es dürfen nur Buchstaben enthalten sein")
    private String translation;

    private VocabularyStatistic vocabularyStatistic;

    public Vocabulary(String expression, String translation) {
        this.expression = expression;
        this.translation = translation;
        this.vocabularyStatistic = new VocabularyStatistic();
    }
    public Vocabulary(){}

    public String getId() {
        return id;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public VocabularyStatistic getVocabularyStatistic() {
        return this.vocabularyStatistic;
    }
}
