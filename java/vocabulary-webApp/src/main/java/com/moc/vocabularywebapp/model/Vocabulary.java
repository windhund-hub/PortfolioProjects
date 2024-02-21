package com.moc.vocabularywebapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;


@Entity
public class Vocabulary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique=true)
    private Integer id;

    @Column(unique=true)
    @NotEmpty(message = "Expression muss gesetzt werden.")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Es dürfen nur Buchstaben enthalten sein")
    private String expression;

    @Column(unique=true)
    @NotEmpty(message = "Translation muss gesetzt werden.")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Es dürfen nur Buchstaben enthalten sein")
    private String translation;

    @OneToOne
    @JoinColumn
    private VocabularyStatistic vocabularyStatistic;

    public Vocabulary(String expression, String translation){

        this.expression = expression;
        this.translation = translation;
        vocabularyStatistic = new VocabularyStatistic();
    }

    public Vocabulary() {

    }

    public Integer getId() {
        return this.id;
    }

    public String getExpression(){

            return this.expression;
    }

    public String getTranslation(){

        return this.translation;
    }

    public VocabularyStatistic getVocabularyStatistic(){

        return this.vocabularyStatistic;
    }

}
