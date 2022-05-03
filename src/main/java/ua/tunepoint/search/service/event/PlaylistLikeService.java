package ua.tunepoint.search.service.event;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;
import ua.tunepoint.search.document.Playlist;
import ua.tunepoint.search.document.User;
import ua.tunepoint.search.document.agg.ListenAggregation;
import ua.tunepoint.search.document.agg.PlaylistLikeAggregation;
import ua.tunepoint.search.document.event.ListenEvent;
import ua.tunepoint.search.document.event.PlaylistLikeEvent;
import ua.tunepoint.search.document.ranking.PlaylistLikeRanking;
import ua.tunepoint.search.document.ranking.UserFollowRanking;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PlaylistLikeService {

    private final ElasticsearchOperations operations;

    public void update(String index) {
        var aggregation = aggregation(index);

        updatePlaylists(
                aggregation.getPlaylist()
        );
    }

    private void updatePlaylists(List<PlaylistLikeRanking> playlists) {
        if (playlists == null || playlists.isEmpty()) {
            return;
        }

        List<UpdateQuery> updateQueries = new LinkedList<>();
        for (var playlist: playlists) {
            var params = Map.<String, Object>of(
                    "like_delta", playlist.getLikeDelta()
            );

            UpdateQuery query = UpdateQuery.builder(playlist.getPlaylistId())
                    .withScript("""
                            if (ctx._source.like_count == null) {
                                ctx._source.like_count = params.like_delta
                            } else {
                                ctx._source.like_count += params.like_delta
                            }
                            """)
                    .withParams(params)
                    .build();

            updateQueries.add(query);
        }

        operations.bulkUpdate(updateQueries, Playlist.class);
    }

    public PlaylistLikeAggregation aggregation(String index) {

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withAggregations(
                        AggregationBuilders.terms("playlist").field("playlist_id")
                                .subAggregation(
                                        AggregationBuilders.sum("like_delta").field("delta")
                                )
                )
                .build();

        SearchHits<PlaylistLikeEvent> response;
        try {
            response = operations.search(query, PlaylistLikeEvent.class, IndexCoordinates.of(index));
        } catch (ElasticsearchStatusException ex) {
            if (ex.status().equals(RestStatus.NOT_FOUND)) {
                return new PlaylistLikeAggregation();
            }
            throw ex;
        }

        if (response.hasAggregations()) {
            Aggregations aggregations = (Aggregations) Objects.requireNonNull(response.getAggregations()).aggregations();

            Terms playlist = aggregations.get("playlist");

            var playlistRankings = new LinkedList<PlaylistLikeRanking>();

            for (var bucket: playlist.getBuckets()) {

                String id = bucket.getKeyAsString();
                Long delta = (long) ((ParsedSum) bucket.getAggregations().get("like_delta"))
                        .getValue();

                playlistRankings.add(new PlaylistLikeRanking(id, delta));
            }

            return new PlaylistLikeAggregation(playlistRankings);
        }

        return new PlaylistLikeAggregation(null); // empty
    }
}
