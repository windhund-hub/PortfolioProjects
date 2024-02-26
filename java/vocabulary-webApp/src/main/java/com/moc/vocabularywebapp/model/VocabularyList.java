package com.moc.vocabularywebapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "userLists")
public class VocabularyList {

    @Id
    private String id;
    private String userId; // Die ID des Benutzers, der die Liste besitzt

    private List<Vocabulary> vocabularies = new ArrayList<>();
    private String listName;

    //erfolgt normallerweise beim einloggen
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setListName(String listName){
        this.listName = userId + "_" + listName;
    }

    public String getListName(){
        return this.listName;
    }

    public List<Vocabulary> getVocabularies() {
        return vocabularies;
    }

    public void setVocabularies(List<Vocabulary> vocabularies) {
        this.vocabularies = vocabularies;
    }



}
