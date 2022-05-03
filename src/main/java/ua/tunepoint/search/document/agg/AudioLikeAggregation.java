package ua.tunepoint.search.document.agg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.tunepoint.search.document.ranking.AudioLikeRanking;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AudioLikeAggregation {

    private List<AudioLikeRanking> audios;
}
