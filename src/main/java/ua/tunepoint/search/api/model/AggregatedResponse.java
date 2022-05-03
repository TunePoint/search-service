package ua.tunepoint.search.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.tunepoint.account.model.response.payload.UserPublicPayload;
import ua.tunepoint.audio.model.response.payload.AudioPayload;
import ua.tunepoint.audio.model.response.payload.PlaylistPayload;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggregatedResponse {

    private List<UserPublicPayload> users;
    private List<PlaylistPayload> playlists;
    private List<AudioPayload> audio;
}
