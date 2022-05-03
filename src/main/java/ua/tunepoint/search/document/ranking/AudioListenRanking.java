package ua.tunepoint.search.document.ranking;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AudioListenRanking {

    private String audioId;
    private Long listeningDelta;
}
