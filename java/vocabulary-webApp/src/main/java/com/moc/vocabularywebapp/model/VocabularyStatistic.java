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
    private int anzahl_trainings = 0;
    @Column
    @NotNull
    private int anzahl_erfolgreich = 0;

    public VocabularyStatistic(int anzahl_trainings,int anzahl_erfolgreich ) {
       this.anzahl_trainings = anzahl_trainings;
       this.anzahl_erfolgreich = anzahl_erfolgreich;
    }
    public VocabularyStatistic(){

    }

    public void updateTraining(boolean erfolg) {

        if (erfolg) {
            this.anzahl_erfolgreich++;
        }

        this.anzahl_trainings++;
    }
    public void setNumberOfTraining(int anzahl_trainings){
        this.anzahl_trainings = anzahl_trainings;
    }

    public void setNumberOfSuccess(int anzahl_erfolgreich){
        this.anzahl_erfolgreich = anzahl_erfolgreich;
    }

    public int getSuccessNumber(){
        return this.anzahl_erfolgreich;
    }

    public double getSuccessRate() {

        if (this.anzahl_trainings == 0) {
            return 0.0;
        }

        return (double) this.anzahl_erfolgreich / (double) this.anzahl_trainings;
    }

    public int getTrainingsRate() {
        return this.anzahl_trainings;
    }


    public Integer getId() {
        return id;
    }
}