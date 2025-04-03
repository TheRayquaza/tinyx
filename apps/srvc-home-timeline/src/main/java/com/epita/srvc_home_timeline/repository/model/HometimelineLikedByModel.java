package com.epita.srvc_home_timeline.repository.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.ejb.Local;
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
@MongoEntity(collection = "HomeTimelineLikedByModel", database = "SrvcHomeTimeline")
public class HometimelineLikedByModel {
    @BsonId
    private String id;
    private String userId;
    private LocalDateTime likedAt;
}
