package ua.tunepoint.search.document.agg;

import lombok.AllArgsConstructor;
import lombok.Data;
import ua.tunepoint.search.document.ranking.UserFollowRanking;

import java.util.List;

@Data
@AllArgsConstructor
public class FollowAggregation {

    private List<UserFollowRanking> user;
}
