package ua.tunepoint.search.service.search;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.RestStatusException;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import ua.tunepoint.search.api.model.ElasticScroll;
import ua.tunepoint.search.config.Indices;
import ua.tunepoint.search.document.User;
import ua.tunepoint.web.exception.BadRequestException;

import static ua.tunepoint.search.config.ElasticConfiguration.SCROLL_TIME_MS;

@Service
@RequiredArgsConstructor
public class UserSearchService {

    private static final String SCRIPT_FUNCTION = """
          _score \
          * (
                doc['follower_count'].empty ? 1 : \
                 ( \
                    doc['follower_count'].value < 2 ? 1 : \
                        Math.log(doc['follower_count'].value) \
                 ) \
            ) \
          * ( \
                doc['listening_count'].empty ? 1 : \
                (\
                    doc['listening_count'].value < 10 ? 1 :\
                        Math.log10(doc['listening_count'].value) \
                ) \
            )\
            """;

    private final ElasticsearchOperations operations; // if it's not here, template can't be injected. why? i have no clue
    private final ElasticsearchRestTemplate template;

    public ElasticScroll<User> searchUser(String searchQuery, Integer pageSize) {
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
                                ScoreFunctionBuilders.scriptFunction(SCRIPT_FUNCTION)
                        )
                )
                .withPageable(Pageable.ofSize(pageSize))
                .build();

        var scroll = template.searchScrollStart(SCROLL_TIME_MS, multimatchQuery, User.class, IndexCoordinates.of(Indices.USER_INDEX));

        return new ElasticScroll<>(scroll);
    }

    public ElasticScroll<User> search(String scrollId) {
        try {
            var scroll = template.searchScrollContinue(scrollId, SCROLL_TIME_MS, User.class, IndexCoordinates.of(Indices.USER_INDEX));
            return new ElasticScroll<>(scroll);
        } catch (RestStatusException ex) {
            throw new BadRequestException("Scroll id " + scrollId + " is not valid");
        }
    }
}
