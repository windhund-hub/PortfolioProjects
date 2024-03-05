package com.moc.vocabularywebapp.model;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "user_vocabularies")
@TypeAlias("UserVocabularies")
public class UserVocabulary extends Vocabulary{

    private String userId;
    @DBRef
    private UserVocabularyList userVocabularyList;

    public UserVocabulary(String userId, UserVocabularyList userVocabularyList, String expression, String translation) {
        super(expression, translation);
        this.userId = userId;
        this.userVocabularyList = userVocabularyList;
    }
}


