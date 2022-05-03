package ua.tunepoint.search.document.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AudioLikeEvent {

    @JsonProperty("audio_id")
    private Long audioId;

    @JsonProperty("audio_owner_id")
    private Long audioOwnerId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("delta")
    private Integer delta;

    public static AudioLikeEvent create(Long audioId, Long audioOwnerId, Long userId, Integer delta) {
        return new AudioLikeEvent(
                audioId, audioOwnerId, userId, delta
        );
    }
}
