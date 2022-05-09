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
import ua.tunepoint.search.document.Audio;
import ua.tunepoint.web.exception.BadRequestException;

import static ua.tunepoint.search.config.ElasticConfiguration.SCROLL_TIME_MS;

@Service
@RequiredArgsConstructor
public class AudioSearchService {

    private static final String SCRIPT_FUNCTION = """
            _score \
            * ( \
                  doc['listening_count'].size() == 0 || doc['listening_count'].value == null ? 1 : \
                  ( \
                      doc['listening_count'].value < 10 ? 1 : \
                          Math.log10(doc['listening_count'].value) \
                  ) \
              ) \
            * ( \
                  doc['like_count'].size() == 0 || doc['listening_count'].value == null? 1 : \
                  ( \
                      doc['like_count'].value < 2 ? 1 : \
                          Math.log(doc['like_count'].value) \
                  ) \
              )\
              """;

    private final ElasticsearchOperations operations; // if it's not here, template can't be injected. why? i have no clue
    private final ElasticsearchRestTemplate template;

    public ElasticScroll<Audio> search(String searchQuery, Integer pageSize) {

        var multimatchQuery = new NativeSearchQueryBuilder()
                .withFilter(QueryBuilders.termQuery("is_private", false))
                .withQuery(
                        QueryBuilders.functionScoreQuery(
                                QueryBuilders.multiMatchQuery(searchQuery)
                                        .field("title", 3).field("title.prefix", 1)
                                        .field("author_pseudonym", 3).field("author_pseudonym.prefix", 1)
                                        .field("description", 1f)
                                        .fuzziness("3"),
                                ScoreFunctionBuilders.scriptFunction(SCRIPT_FUNCTION)
                        )
                )
                .withPageable(Pageable.ofSize(pageSize))
                .build();

        var scroll = template.searchScrollStart(
                SCROLL_TIME_MS, multimatchQuery, Audio.class, IndexCoordinates.of(Indices.AUDIO_INDEX)
        );

        return new ElasticScroll<>(scroll);
    }

    public ElasticScroll<Audio> search(String scrollId) {
        try {
            var scroll = template.searchScrollContinue(scrollId, SCROLL_TIME_MS, Audio.class, IndexCoordinates.of(Indices.AUDIO_INDEX));
            return new ElasticScroll<>(scroll);
        } catch (RestStatusException ex) {
            throw new BadRequestException("Scroll id " + scrollId + " is not valid");
        }
    }
}
