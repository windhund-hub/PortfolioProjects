package com.moc.vocabularywebapp.model;

import jakarta.validation.constraints.NotNull;

public class VocabularyStatistic {

    @NotNull
    private int numberOfTraining;
    @NotNull
    private int numberOfSuccess;

    public VocabularyStatistic() {
        this.numberOfTraining = 0;
        this.numberOfSuccess = 0;
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
