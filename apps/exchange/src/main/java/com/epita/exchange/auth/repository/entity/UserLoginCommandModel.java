package com.epita.exchange.auth.repository.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import lombok.*;

import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
@Builder
public class UserLoginCommandModel extends PanacheMongoEntity {
    @Id private String userId;
    private String bearerToken;
    private boolean authenticated = false; 
}
