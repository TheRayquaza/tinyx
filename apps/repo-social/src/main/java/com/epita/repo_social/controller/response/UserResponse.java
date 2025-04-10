package com.epita.repo_social.controller.response;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class UserResponse {
  private String id;
  private String username;
  private String email;
  private String bio;
  private String profileImage;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private boolean deleted;
}
