package com.epita.repo_user.controller.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@With
@NoArgsConstructor
@Getter
@Setter
public class LoginRequest {
    @NotNull public String username;
    @NotNull public String password;
}
