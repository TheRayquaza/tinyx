package com.epita.exchange.auth.service.entity;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthEntity implements Serializable {
  private String userId;
  private String username;
}
