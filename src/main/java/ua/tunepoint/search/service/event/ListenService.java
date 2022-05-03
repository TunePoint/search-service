package ua.tunepoint.search.service.event;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;
import ua.tunepoint.search.config.props.AggregationProperties;
import ua.tunepoint.search.document.Audio;
import ua.tunepoint.search.document.event.ListenEvent;
import ua.tunepoint.search.document.User;
import ua.tunepoint.search.document.agg.ListenAggregation;
import ua.tunepoint.search.document.ranking.AudioListenRanking;
import ua.tunepoint.search.document.ranking.UserListenRanking;
import ua.tunepoint.search.service.ElasticService;
import ua.tunepoint.search.service.TimeIndexProvider;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static ua.tunepoint.search.config.Indices.LISTEN_EVENT_INDEX;

@Service
@RequiredArgsConstructor
public class ListenService {

    private final ElasticsearchRestTemplate template;

    public void update(String index) {

        ListenAggregation aggregation = aggregate(index);

        updateUsers(
                aggregation.getUser()
        );

        updateAudios(
                aggregation.getAudio()
        );
    }

    public void updateAudios(List<AudioListenRanking> audios) {
        if (audios == null || audios.isEmpty()) {
            return;
        }

        List<UpdateQuery> updateQueries = new LinkedList<>();

        for (var audio : audios) {
            var params = Map.<String, Object>of(
                    "listening_delta", audio.getListeningDelta()
            );

            UpdateQuery updateQuery = UpdateQuery.builder(audio.getAudioId())
                    .withScript("""
                            if (ctx._source.listening_count == null) {
                                ctx._source.listening_count = params.listening_delta
                            } else {
                                ctx._source.listening_count += params.listening_delta
                            }
                            """)
                    .withParams(params)
                    .build();

            updateQueries.add(updateQuery);
        }

        template.bulkUpdate(updateQueries, Audio.class);
    }

    public void updateUsers(List<UserListenRanking> users) {
        if (users == null || users.isEmpty()) {
            return;
        }

        List<UpdateQuery> updateQueries = new LinkedList<>();

        for (var user : users) {
            var params = Map.<String, Object>of(
                    "listening_delta", user.getListeningDelta()
            );

            UpdateQuery updateQuery = UpdateQuery.builder(user.getUserId())
                    .withScript("""
                            if (ctx._source.listening_count == null) {
                                ctx._source.listening_count = params.listening_delta
                            } else {
                                ctx._source.listening_count += params.listening_delta
                            }
                            """)
                    .withParams(params)
                    .build();

            updateQueries.add(updateQuery);
        }

        template.bulkUpdate(updateQueries, User.class);
    }

    public ListenAggregation aggregate(String index) {

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withAggregations(
                        AggregationBuilders.terms("owner_listening").field("audio_owner_id"),
                        AggregationBuilders.terms("audio_listening").field("audio_id")
                )
                .build();

        SearchHits<ListenEvent> response;
        try {
            response = template.search(query, ListenEvent.class, IndexCoordinates.of(index));
        } catch (ElasticsearchStatusException ex) {
            if (ex.status().equals(RestStatus.NOT_FOUND)) {
                return new ListenAggregation(null, null);
            }
            throw ex;
        }

        if (response.hasAggregations()) {
            Aggregations aggregations = (Aggregations) Objects.requireNonNull(response.getAggregations()).aggregations();

            return new ListenAggregation(
                    rankings(
                            aggregations.get("audio_listening"),
                            bucket -> new AudioListenRanking(
                                    bucket.getKeyAsString(),
                                    bucket.getDocCount()
                            )
                    ),
                    rankings(
                            aggregations.get("owner_listening"),
                            bucket -> new UserListenRanking(
                                    bucket.getKeyAsString(),
                                    bucket.getDocCount()
                            )
                    )
            );
        }

        return new ListenAggregation(null, null);
    }

    private <T> List<T> rankings(Terms agg, Function<Terms.Bucket, T> converter) {
        final List<T> rankings = new LinkedList<>();
        for (var bucket : agg.getBuckets()) {
            rankings.add(
                    converter.apply(bucket)
            );
        }
        return rankings;
    }
}
