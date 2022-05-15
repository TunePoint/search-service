package ua.tunepoint.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.info("Aggregating audio listenings...");

        try {
            final var now = LocalDateTime.now();
            listenService.update(indexProvider.previous(Indices.LISTEN_EVENT_INDEX, now));
        } catch (Exception ex) {
            log.error("Error occurred while aggregating audio listenings", ex);
        }
    }

    @Transactional
    @Scheduled(cron = "${aggregation.cron}")
    public void aggregateFollow() {
        log.info("Aggregating user followings ...");

        try {
            final var now = LocalDateTime.now();
            followService.update(indexProvider.previous(Indices.FOLLOW_EVENT_INDEX, now));
        } catch (Exception ex) {
            log.error("Error occurred while aggregating user followings", ex);
        }
    }

    @Transactional
    @Scheduled(cron = "${aggregation.cron}")
    public void aggregateAudioLike() {
        log.info("Aggregating audio likes ...");


        try {
            final var now = LocalDateTime.now();
            audioLikeService.update(indexProvider.previous(Indices.AUDIO_LIKE_EVENT_INDEX, now));
        } catch (Exception ex) {
            log.error("Error occurred while aggregating audio likes", ex);
        }
    }

    @Transactional
    @Scheduled(cron = "${aggregation.cron}")
    public void aggregatePlaylistLike() {
        log.info("Aggregating playlist likes ...");

        try {
            final var now = LocalDateTime.now();
            playlistLikeService.update(indexProvider.previous(Indices.PLAYLIST_LIKE_EVENT_INDEX, now));
        } catch (Exception ex) {
            log.error("Error occurred while aggregating playlist likes", ex);
        }
    }
}
