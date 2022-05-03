package ua.tunepoint.search.document.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListenEvent {

    @JsonProperty("audio_id")
    private Long audioId;

    @JsonProperty("audio_owner_id")
    private Long audioOwnerId;

    @JsonProperty("user_id")
    private Long userId;

    public static ListenEvent create(Long audioId, Long audioOwnerId, Long userId) {
        return new ListenEvent(
                audioId,
                audioOwnerId,
                userId
        );
    }
}
