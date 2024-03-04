package com.moc.vocabularywebapp.repository;

import com.moc.vocabularywebapp.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SecurityRepository extends MongoRepository<User, String> {
    User findByUsername(String username);

}

