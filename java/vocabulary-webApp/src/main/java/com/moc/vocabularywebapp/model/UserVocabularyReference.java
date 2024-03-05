package com.moc.vocabularywebapp.model;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_vocabularies")
@TypeAlias("UserVocabularies")
public class UserVocabularyReference{


    private String userId;
    @DBRef
    private Vocabulary vocabulary;
    @DBRef
    private UserVocabularyList userVocabularyList;

    public UserVocabularyReference(String userId, UserVocabularyList userVocabularyList, Vocabulary vocabulary){
        this.userId = userId;
        this.userVocabularyList = userVocabularyList;
        this.vocabulary = vocabulary;
    }

}
