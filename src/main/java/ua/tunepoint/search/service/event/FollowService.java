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
import ua.tunepoint.search.document.agg.ListenAggregation;
import ua.tunepoint.search.document.event.FollowEvent;
import ua.tunepoint.search.document.User;
import ua.tunepoint.search.document.agg.FollowAggregation;
import ua.tunepoint.search.document.event.ListenEvent;
import ua.tunepoint.search.document.ranking.UserFollowRanking;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final ElasticsearchRestTemplate elasticTemplate;

    public void update(String index) {
        FollowAggregation aggregation = aggregate(index);

        updateUsers(
                aggregation.getUser()
        );
    }

    private void updateUsers(List<UserFollowRanking> users) {
        if (users == null || users.isEmpty()) {
            return;
        }

        List<UpdateQuery> updateQueries = new LinkedList<>();
        for (var user: users) {
            var params = Map.<String, Object>of(
                    "follower_delta", user.getFollowerDelta()
            );

            UpdateQuery query = UpdateQuery.builder(user.getUserId())
                    .withScript("""
                            if (ctx._source.follower_count == null) {
                                ctx._source.follower_count = params.follower_delta
                            } else {
                                ctx._source.follower_count += params.follower_delta
                            }
                            """)
                    .withParams(params)
                    .build();

            updateQueries.add(query);
        }

        elasticTemplate.bulkUpdate(updateQueries, User.class);
    }

    public FollowAggregation aggregate(String index) {

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withAggregations(
                        AggregationBuilders.terms("following").field("following_id")
                                .subAggregation(
                                        AggregationBuilders.sum("follower_delta").field("delta")
                                )
                )
                .build();

        SearchHits<FollowEvent> response;
        try {
            response = elasticTemplate.search(query, FollowEvent.class, IndexCoordinates.of(index));
        } catch (ElasticsearchStatusException ex) {
            if (ex.status().equals(RestStatus.NOT_FOUND)) {
                return new FollowAggregation(null);
            }
            throw ex;
        }

        if (response.hasAggregations()) {
            Aggregations aggregations = (Aggregations) Objects.requireNonNull(response.getAggregations()).aggregations();

            Terms terms = aggregations.get("following");

            List<UserFollowRanking> users = new LinkedList<>();

            for (var bucket : terms.getBuckets()) {
                String id = bucket.getKeyAsString();

                Aggregations followerAggregation = aggregations(bucket);
                Long sum = aggregatedSum(
                        followerAggregation.get("follower_delta")
                );

                users.add(new UserFollowRanking(id, sum));
            }

            return new FollowAggregation(users);
        }

        return new FollowAggregation(null);
    }

    private long aggregatedSum(ParsedSum agg) {
        return (long) agg.getValue();
    }

    private Aggregations aggregations(Terms.Bucket bucket) {
        return bucket.getAggregations();
    }
}
