package com.epita.repo_user.controller.request;

import lombok.*;

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
