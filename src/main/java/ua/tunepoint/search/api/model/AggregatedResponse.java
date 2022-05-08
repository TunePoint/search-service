package ua.tunepoint.search.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import ua.tunepoint.account.model.response.payload.UserPublicPayload;
import ua.tunepoint.audio.model.response.payload.AudioPayload;
import ua.tunepoint.audio.model.response.payload.PlaylistPayload;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggregatedResponse {

    private Page<UserPublicPayload> users;
    private Page<PlaylistPayload> playlists;
    private Page<AudioPayload> audio;
}
