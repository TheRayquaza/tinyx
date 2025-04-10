package com.epita.srvc_user_timeline.controller.contract;

import java.io.Serializable;
import java.util.List;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
public class UserTimelineResponse implements Serializable {
  private List<UserTimelinePostResponse> posts;
}
