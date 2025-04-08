package com.epita.srvc_home_timeline.service.entity;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@With
public class HomeTimelinePostEntity implements Serializable {
	@BsonId
    private ObjectId id;
  	// we add this field, compared to post model in repo-post, to store the user id to which this post
  	// belongs
  	private String postId;
  	private String ownerId;
  	private String text;
  	private String media;
  	private String repostIdString;
  	private String replyToPostId;
  	private boolean isReply;
  	private LocalDateTime createdAt;
  	private LocalDateTime updatedAt;
  	private boolean deleted;

}
