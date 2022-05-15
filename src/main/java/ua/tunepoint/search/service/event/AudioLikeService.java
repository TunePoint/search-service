package ua.tunepoint.search.service.event;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;
import ua.tunepoint.search.document.Audio;
import ua.tunepoint.search.document.event.AudioLikeEvent;
import ua.tunepoint.search.document.event.FollowEvent;
import ua.tunepoint.search.document.agg.AudioLikeAggregation;
import ua.tunepoint.search.document.ranking.AudioLikeRanking;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AudioLikeService {

    private final ElasticsearchRestTemplate elasticTemplate;

    public void update(String index) {
        AudioLikeAggregation aggregation = aggregate(index);

        updateAudios(
                aggregation.getAudios()
        );
    }

    public void updateAudios(List<AudioLikeRanking> audios) {
        if (audios == null || audios.isEmpty()) {
            return;
        }

        List<UpdateQuery> updateQueries = new LinkedList<>();

        for (var audio: audios) {
            var params = Map.<String, Object>of(
                    "like_delta", audio.getLikeDelta()
            );

            UpdateQuery updateQuery = UpdateQuery.builder(audio.getAudioId())
                    .withScript("""
                            if (ctx._source.like_count == null) {
                                ctx._source.like_count = params.like_delta
                            } else {
                                ctx._source.like_count += params.like_delta
                            }
                            """)
                    .withParams(params)
                    .build();

            updateQueries.add(updateQuery);
        }

        elasticTemplate.bulkUpdate(updateQueries, Audio.class);
    }

    public AudioLikeAggregation aggregate(String index) {

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withAggregations(
                        AggregationBuilders.terms("audio_like").field("audio_id")
                                .subAggregation(
                                        AggregationBuilders.sum("like_delta").field("delta")
                                )
                )
                .build();

        SearchHits<AudioLikeEvent> response;
        try {
            response = elasticTemplate.search(query, AudioLikeEvent.class, IndexCoordinates.of(index));
        } catch (ElasticsearchStatusException ex) {
            if (ex.status().equals(RestStatus.NOT_FOUND)) {
                return new AudioLikeAggregation();
            }
            throw ex;
        }

        if (response.hasAggregations()) {
            Aggregations aggregations = (Aggregations) Objects.requireNonNull(response.getAggregations()).aggregations();

            List<AudioLikeRanking> audios = new LinkedList<>();

            Terms terms = aggregations.get("audio_like");

            for (var bucket: terms.getBuckets()) {
                String id = bucket.getKeyAsString();

                Aggregations likeAggregation = bucket.getAggregations();
                Long likeDelta = aggregatedSum(
                        likeAggregation.get("like_delta")
                );

                audios.add(new AudioLikeRanking(id, likeDelta));
            }

            return new AudioLikeAggregation(
                    audios
            );
        }

        return new AudioLikeAggregation(); // empty
    }

    private long aggregatedSum(ParsedSum agg) {
        return (long) agg.getValue();
    }
}
