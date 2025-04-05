package com.epita.srvc_home_timeline.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import com.epita.srvc_home_timeline.service.entity.HomeTimelineEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@With
public class HomeTimelineResponse {
    private HomeTimelineEntity hometimeline;
}
