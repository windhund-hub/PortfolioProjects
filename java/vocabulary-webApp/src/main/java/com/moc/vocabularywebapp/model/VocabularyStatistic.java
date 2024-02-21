package com.moc.vocabularywebapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class VocabularyStatistic {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column
    @NotNull
    private int numberOfTraining;
    @Column
    @NotNull
    private int numberOfSuccess;

    public VocabularyStatistic() {
       this.numberOfTraining = 0;
       this.numberOfSuccess = 0;
    }

    public Integer getId() {
        return id;
    }
    public void setNumberOfTraining(int numberOfTraining){
        this.numberOfTraining = numberOfTraining;
    }

    public void setNumberOfSuccess(int numberOfSuccess){
        this.numberOfSuccess = numberOfSuccess;
    }

    public int getNumberOfSuccess(){
        return this.numberOfSuccess;
    }

    public int getNumberOfTraining() {
        return this.numberOfTraining;
    }

    public double getSuccessRate() {
        if (this.numberOfTraining == 0) {
            return 0.0;
        }
        return (double) this.numberOfSuccess / (double) this.numberOfTraining;
    }

}