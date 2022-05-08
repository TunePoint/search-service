package ua.tunepoint.search.service.search;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import ua.tunepoint.search.document.Playlist;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistSearchService {

    private final ElasticsearchOperations operations;

    public Page<Playlist> search(String queryString, Pageable pageable) {

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withFilter(QueryBuilders.termQuery("is_private", false))
                .withQuery(
                        QueryBuilders.functionScoreQuery(
                                QueryBuilders.multiMatchQuery(queryString)
                                        .field("title", 4).field("title.prefix", 1)
                                        .field("author_pseudonym", 2).field("author_pseudonym.prefix", 1)
                                        .field("description", 1.5f).field("description.prefix", 1.1f),
                                ScoreFunctionBuilders.scriptFunction("""
                                        _score *
                                          * ( \
                                                doc['listening_count'].size() == 0 ||  doc['listening_count'].value == null ? 1 : \
                                                ( \
                                                    doc['listening_count'].value < 10 ? 1 : \
                                                        Math.log10(doc['listening_count'].value) \
                                                ) \
                                            ) \
                                          * ( \
                                                doc['like_count'].size() == 0 ||  doc['listening_count'].value == null? 1 : \
                                                ( \
                                                    doc['like_count'].value < 2 ? 1 : \
                                                        Math.log(doc['like_count'].value) \
                                                ) \
                                            )\
                                        """)
                        )
                )
                .withPageable(pageable)
                .build();

        var hits = operations.searchForStream(query, Playlist.class);

        return new PageImpl<>(
                hits.stream().map(SearchHit::getContent).collect(Collectors.toList()),
                pageable,
                hits.getTotalHits()
        );
    }
}
