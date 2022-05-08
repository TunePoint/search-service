package ua.tunepoint.search.service.search;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import ua.tunepoint.search.document.Audio;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AudioSearchService {

    private final ElasticsearchOperations operations;

    public Page<Audio> search(String searchQuery, Pageable pageable) {

        var multimatchQuery = new NativeSearchQueryBuilder()
                .withFilter(QueryBuilders.termQuery("is_private", false))
                .withQuery(
                        QueryBuilders.functionScoreQuery(
                                QueryBuilders.multiMatchQuery(searchQuery)
                                        .field("title", 3).field("title.prefix", 1)
                                        .field("author_pseudonym", 3).field("author_pseudonym.prefix", 1)
                                        .field("description", 1f)
                                        .fuzziness("3"),
                                ScoreFunctionBuilders.scriptFunction(
                                        """
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
                                        """
                                )
                        )
                )
                .withPageable(pageable)
                .build();

        var hits = operations.searchForStream(multimatchQuery, Audio.class);

        return new PageImpl<>(
                hits.stream().map(SearchHit::getContent).collect(Collectors.toList()),
                pageable,
                hits.getTotalHits()
        );
    }
}
