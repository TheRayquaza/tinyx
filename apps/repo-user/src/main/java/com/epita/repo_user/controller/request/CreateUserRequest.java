package com.epita.repo_user.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@With
@NoArgsConstructor
@Getter
@Setter
public class CreateUserRequest {
    @NotNull
    public String username;
    @NotNull public String password;
    public String email;
}
