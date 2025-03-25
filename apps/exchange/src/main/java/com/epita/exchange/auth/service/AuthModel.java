package com.epita.exchange.auth.service;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthModel implements Serializable {
    private String userId;
    private String username;
    private String email;
}
