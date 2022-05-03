package ua.tunepoint.search.document.ranking;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserFollowRanking {

    private String userId;
    private Long followerDelta;
}
