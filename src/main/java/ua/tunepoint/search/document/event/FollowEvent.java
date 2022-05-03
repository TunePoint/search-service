package ua.tunepoint.search.document.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowEvent {

    @JsonProperty("following_id")
    private Long followingId;

    @JsonProperty("follower_id")
    private Long followerId;

    /**
     * +1 or -1 (follow / unfollow)
     */
    @JsonProperty("delta")
    private Integer delta;
}
