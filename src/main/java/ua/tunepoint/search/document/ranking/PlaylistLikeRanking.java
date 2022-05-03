package ua.tunepoint.search.document.ranking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistLikeRanking {

    private String playlistId;
    private Long likeDelta;
}
