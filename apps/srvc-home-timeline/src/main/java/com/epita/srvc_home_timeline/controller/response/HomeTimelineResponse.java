package com.epita.srvc_home_timeline.controller.response;

import com.epita.srvc_home_timeline.service.entity.HomeTimelineEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@With
public class HomeTimelineResponse {
  private HomeTimelineEntity hometimeline;
}
