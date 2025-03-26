package com.epita.repo_user.repository;

import com.epita.repo_user.repository.model.UserModel;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheMongoRepository<UserModel> {
  public Optional<UserModel> findByUsername(String username) {
    return find("username", username).firstResultOptional();
  }
}
