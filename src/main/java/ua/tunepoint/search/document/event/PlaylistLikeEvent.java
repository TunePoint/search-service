package ua.tunepoint.search.document.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistLikeEvent {

    @JsonProperty("playlist_id")
    private Long playlistId;

    @JsonProperty("playlist_owner_id")
    private Long playlistOwnerId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("delta")
    private Integer delta;

    public static PlaylistLikeEvent create(Long playlistId, Long playlistOwnerId, Long userId, Integer delta) {
        return new PlaylistLikeEvent(
                playlistId,
                playlistOwnerId,
                userId,
                delta
        );
    }
}
