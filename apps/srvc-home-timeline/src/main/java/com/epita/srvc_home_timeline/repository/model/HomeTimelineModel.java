package com.epita.srvc_home_timeline.repository.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "HomeTimelineModel", database = "SrvcHomeTimeline")
public class HomeTimelineModel {
    @BsonId private ObjectId id;
}
