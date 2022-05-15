package ua.tunepoint.search.service.search;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.RestStatusException;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import ua.tunepoint.search.api.model.ElasticScroll;
import ua.tunepoint.search.config.Indices;
import ua.tunepoint.search.document.Playlist;
import ua.tunepoint.web.exception.BadRequestException;

import static ua.tunepoint.search.config.ElasticConfiguration.SCROLL_TIME_MS;

@Service
@RequiredArgsConstructor
public class PlaylistSearchService {

    private final static String SCRIPT_FUNCTION = """
            _score \
            * ( \
                  (doc['like_count'].size() == 0 || doc['like_count'].value == null) ? 1 : \
                  ( \
                      doc['like_count'].value < 3 ? 1 : \
                          Math.log(doc['like_count'].value) \
                  ) \
              )\
            """;

    private final ElasticsearchOperations operations; // if it's not here, template can't be injected. why? i have no clue
    private final ElasticsearchRestTemplate template;

    public ElasticScroll<Playlist> search(String queryString, Integer pageSize) {

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withFilter(QueryBuilders.termQuery("is_private", false))
                .withQuery(
                        QueryBuilders.functionScoreQuery(
                                QueryBuilders.multiMatchQuery(queryString)
                                        .field("title", 4).field("title.prefix", 1)
                                        .field("description", 1.5f).field("description.prefix", 1.1f),
                                ScoreFunctionBuilders.scriptFunction(SCRIPT_FUNCTION)
                        )
                )
                .withPageable(Pageable.ofSize(pageSize))
                .build();

        var scroll = template.searchScrollStart(SCROLL_TIME_MS, query, Playlist.class, IndexCoordinates.of(Indices.PLAYLIST_INDEX));

        return new ElasticScroll<>(scroll);
    }

    public ElasticScroll<Playlist> search(String scrollId) {
        try {
            var scroll = template.searchScrollContinue(scrollId, SCROLL_TIME_MS, Playlist.class, IndexCoordinates.of(Indices.PLAYLIST_INDEX));
            return new ElasticScroll<>(scroll);
        } catch (RestStatusException ex) {
            throw new BadRequestException("Scroll id " + scrollId + " is not valid");
        }
    }
}
