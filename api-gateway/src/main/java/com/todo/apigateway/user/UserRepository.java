package com.todo.apigateway.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, Integer> {

  List<User> findAllByRole(String role);
  Optional<User> findByEmail(String email);

  Integer countByRole(String role);
}
