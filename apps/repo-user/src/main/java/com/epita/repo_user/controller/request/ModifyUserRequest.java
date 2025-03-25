package com.epita.repo_user.controller.request;

import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@AllArgsConstructor
@With
@NoArgsConstructor
@Getter
@Setter
public class ModifyUserRequest {
    public String username;
    public String email;
    public String bio;
}
