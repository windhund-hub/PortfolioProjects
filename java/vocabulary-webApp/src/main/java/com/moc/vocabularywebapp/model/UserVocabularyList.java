package com.moc.vocabularywebapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_lists")
@TypeAlias("UserLists")
public class UserVocabularyList {

    @Id
    private String id;
    private String userId;
    private String name;

    public UserVocabularyList(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public String getListName(){
        return this.name;
    }

}
