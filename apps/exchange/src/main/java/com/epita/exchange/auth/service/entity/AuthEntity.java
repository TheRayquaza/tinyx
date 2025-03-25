package com.epita.exchange.auth.service.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthEntity implements Serializable {
    private String userId;
    private String username;
    private String email;
}
