package com.epita.exchange.auth.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import com.epita.exchange.auth.repository.entity.UserLoginCommandModel;

import java.util.UUID;

@ApplicationScoped
public class UserLoginCommandRepository implements PanacheMongoRepository<UserLoginCommandModel> {
    public UserLoginCommandModel findByUserId(UUID userId) {
        return find("userId", userId).firstResult();
    }
}
