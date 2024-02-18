package com.moc.vocabularywebapp.model;

import jakarta.persistence.*;


@Entity
public class Vocabulary {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String expression;
    @Column
    private String translation;

    @OneToOne
    @JoinColumn
    private VocabularyStatistic vocabularyStatistic;

    public Vocabulary(String expression, String translation){

        this.expression = expression;
        this.translation = translation;
        //vocabularyStatistic = new VocabularyStatistic();
    }

    public Vocabulary() {

    }
    public void setId(Integer id){
        this.id = id;
    }
    public Integer getId() {
        return this.id;
    }

    public String getWord(){

        return this.expression;
    }

    public String getTranslation(){

        return this.translation;

    }

    public VocabularyStatistic getVocabularyStatistic(){

        return this.vocabularyStatistic;
    }

}
