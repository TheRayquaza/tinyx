package com.epita.repo_user.controller.response;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponse implements Serializable {
  public String id;
  public String username;
  public String jwt;
}
