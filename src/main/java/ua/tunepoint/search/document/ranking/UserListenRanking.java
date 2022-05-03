package ua.tunepoint.search.document.ranking;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserListenRanking {

    private String userId;
    private Long listeningDelta;
}
