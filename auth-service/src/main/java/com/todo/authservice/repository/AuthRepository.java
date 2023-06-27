package com.todo.authservice.repository;

import com.todo.authservice.model.LocalUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository  extends MongoRepository<LocalUser, String> {
}
