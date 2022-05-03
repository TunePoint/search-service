package ua.tunepoint.search.document.agg;

import lombok.AllArgsConstructor;
import lombok.Data;
import ua.tunepoint.search.document.ranking.AudioListenRanking;
import ua.tunepoint.search.document.ranking.UserListenRanking;

import java.util.List;

@Data
@AllArgsConstructor
public class ListenAggregation {

    private List<AudioListenRanking> audio;
    private List<UserListenRanking> user;

    public boolean hasAudio() {
        return audio != null && !audio.isEmpty();
    }

    public boolean hasUser() {
        return user != null && !user.isEmpty();
    }
}
