package com.epita.exchange.auth.controller;

import com.epita.exchange.auth.repository.UserLoginCommandRepository;
import com.epita.exchange.auth.repository.entity.UserLoginCommandModel;
import io.quarkus.redis.client.RedisClient;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.epita.exchange.utils.Logger;
import com.epita.exchange.redis.command.UserLoginCommand;

import java.util.UUID;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class AuthServiceSubscriber implements Logger {

    @ConfigProperty(name = "redis.channel.user_login_command")
    String userLoginCommandChannel;

    @Inject
    RedisClient redisClient;

    @Inject
    UserLoginCommandRepository userLoginCommandRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void onStart(@Observes StartupEvent ev) {
        this.logger().info("Starting Auth Service and listening to Redis...");
        listenToUserLoginCommandChannel();
    }

    private void listenToUserLoginCommandChannel() {
        CompletableFuture.runAsync(() -> {
            redisClient.subscribe(List.of(userLoginCommandChannel), message -> {
                handleMessage(message);
            });
        });
    }

    private void handleMessage(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            if ("UserLoginCommand".equals(event.get("eventType").asText())) {
                processUserLoginCommand(event);
            }
        } catch (Exception e) {
            this.logger().error("Error processing message from Redis", e);
        }
    }

    @Transactional
    private void processUserLoginCommand(JsonNode event) {
        UUID userId = UUID.fromString(event.get("userId").asText());
        String bearerToken = event.get("bearerToken").asText();

        UserLoginCommandModel userLoginCommand = new UserLoginCommandModel()
                .withUserId(userId.toString())
                .withBearerToken(bearerToken);

        userLoginCommandRepository.persist(userLoginCommand);
    }
}
