package com.epita.srvc_home_timeline.repository.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "HomeTimelineEntryModel", database = "SrvcHomeTimeline")
public class HomeTimelineEntryModel {
    @BsonId
    private String id;
    private String userId;
    private String authorId;
    private String content;
    // Array of HomeTimelineLikedByModel
    private List<String> likedBy;
    private String type;
    private LocalDateTime timestamp;
}
