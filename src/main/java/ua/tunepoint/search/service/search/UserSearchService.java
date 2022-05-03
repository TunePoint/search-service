package ua.tunepoint.search.service.search;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import ua.tunepoint.search.document.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSearchService {

    private final ElasticsearchOperations client;

    public List<User> searchUser(String searchQuery, Pageable pageable) {
        var multimatchQuery = new NativeSearchQueryBuilder()
                .withQuery(
                        QueryBuilders.functionScoreQuery(
                                QueryBuilders.multiMatchQuery(searchQuery)
                                        .field("username", 3).field("username.prefix", 1)
                                        .field("pseudonym", 3).field("pseudonym.prefix", 1)
                                        .field("first_name", 1.5f).field("first_name.prefix", 1)
                                        .field("last_name", 1.5f).field("last_name.prefix", 1)
                                        .field("bio", 1f)
                                        .fuzziness("2"),
                                ScoreFunctionBuilders.scriptFunction(
                                    """
                                      _score \
                                      * (
                                            doc['follower_count'].empty ? 1 :
                                             (
                                                doc['follower_count'].value < 2 ? 1 :
                                                    Math.log(doc['follower_count'].value)
                                             )
                                        ) \
                                      * (
                                            doc['listening_count'].empty ? 1 :
                                            (
                                                doc['listening_count'].value < 10 ? 1 :
                                                    Math.log10(doc['listening_count'].value)
                                            )
                                        )
                                    """
                                )
                        )
                )
                .withPageable(pageable)
                .build();

        var hits = client.search(multimatchQuery, User.class).getSearchHits();

        return hits.stream().map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
