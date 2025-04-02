package com.epita.srvc_home_timeline.service.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@With
public class HomeTimelineEntity implements Serializable {
    public String id;
}
