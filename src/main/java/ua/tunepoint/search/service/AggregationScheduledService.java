package ua.tunepoint.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.tunepoint.search.config.Indices;
import ua.tunepoint.search.service.event.AudioLikeService;
import ua.tunepoint.search.service.event.FollowService;
import ua.tunepoint.search.service.event.ListenService;
import ua.tunepoint.search.service.event.PlaylistLikeService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AggregationScheduledService {

    private final TimeIndexProvider indexProvider;

    private final AudioLikeService audioLikeService;
    private final PlaylistLikeService playlistLikeService;
    private final FollowService followService;
    private final ListenService listenService;

    @Transactional
    @Scheduled(cron = "${aggregation.cron}")
    public void aggregateAudioListen() {
        final var now = LocalDateTime.now();
        listenService.update(indexProvider.previous(Indices.LISTEN_EVENT_INDEX, now));
    }

    @Transactional
    @Scheduled(cron = "${aggregation.cron}")
    public void aggregateFollow() {
        final var now = LocalDateTime.now();
        followService.update(indexProvider.previous(Indices.FOLLOW_EVENT_INDEX, now));
    }

    @Transactional
    @Scheduled(cron = "${aggregation.cron}")
    public void aggregateAudioLike() {
        final var now = LocalDateTime.now();
        audioLikeService.update(indexProvider.previous(Indices.AUDIO_LIKE_EVENT_INDEX, now));
    }

    @Transactional
    @Scheduled(cron = "${aggregation.cron}")
    public void aggregatePlaylistLike() {
        final var now = LocalDateTime.now();
        playlistLikeService.update(indexProvider.previous(Indices.PLAYLIST_LIKE_EVENT_INDEX, now));
    }
}
