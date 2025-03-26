package com.epita.repo_user.service.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@With
public class UserEntity implements Serializable {
  private String id;
  private String username;
  private String email;
  private String bio;
  private String profileImage;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private boolean deleted;
}
