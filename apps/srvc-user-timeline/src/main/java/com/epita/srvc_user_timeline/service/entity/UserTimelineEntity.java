package com.epita.srvc_user_timeline.service.entity;

import java.io.Serializable;
import java.util.List;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
public class UserTimelineEntity implements Serializable {
  List<UserTimelinePostEntity> posts;
}
