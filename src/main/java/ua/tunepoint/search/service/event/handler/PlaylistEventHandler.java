package ua.tunepoint.search.service.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.tunepoint.audio.model.event.playlist.PlaylistCreatedEvent;
import ua.tunepoint.audio.model.event.playlist.PlaylistDeletedEvent;
import ua.tunepoint.audio.model.event.playlist.PlaylistLikedEvent;
import ua.tunepoint.audio.model.event.playlist.PlaylistUnlikedEvent;
import ua.tunepoint.audio.model.event.playlist.PlaylistUpdatedEvent;
import ua.tunepoint.event.model.DomainEvent;
import ua.tunepoint.search.config.Indices;
import ua.tunepoint.search.config.props.AggregationProperties;
import ua.tunepoint.search.document.Playlist;
import ua.tunepoint.search.document.event.PlaylistLikeEvent;
import ua.tunepoint.search.service.ElasticService;
import ua.tunepoint.search.service.TimeIndexProvider;
import ua.tunepoint.search.service.elastic.PlaylistElasticService;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlaylistEventHandler {

    private final ElasticService elasticService;
    private final PlaylistElasticService playlistElasticService;
    private final AggregationProperties props;

    private final TimeIndexProvider indexProvider;

    public void handlePlaylistLiked(PlaylistLikedEvent event) {
        log(event);

        elasticService.index(
                indexProvider.current(Indices.PLAYLIST_LIKE_EVENT_INDEX, LocalDateTime.now()),
                PlaylistLikeEvent.create(
                        event.getPlaylistId(),
                        event.getPlaylistOwnerId(),
                        event.getUserId(), 1
                )
        );
    }

    public void handlePlaylistUnliked(PlaylistUnlikedEvent event) {
        log(event);
        elasticService.index(
                indexProvider.current(Indices.PLAYLIST_LIKE_EVENT_INDEX, LocalDateTime.now()),
                PlaylistLikeEvent.create(
                        event.getPlaylistId(), event.getPlaylistOwnerId(),
                        event.getUserId(), -1
                )
        );
    }

    public void handlePlaylistCreated(PlaylistCreatedEvent event) {
        log(event);
        playlistElasticService.save(
                new Playlist(
                        event.getPlaylistId(), event.getPlaylistOwnerId(),
                        event.getTitle(), event.getDescription(),
                        event.getIsPrivate(), 0L, 0L
                )
        );
    }

    public void handlePlaylistDeleted(PlaylistDeletedEvent event) {
        log(event);
        playlistElasticService.delete(event.getPlaylistId()); // TODO: look into like events (data about like may be stale)
    }

    public void handlePlaylistUpdated(PlaylistUpdatedEvent event) {
        log(event);
        playlistElasticService.update(
                event.getPlaylistId(),
                event.getTitle(),
                event.getDescription(),
                event.getIsPrivate()
        );
    }

    private void log(DomainEvent event) {
        log.info("Processing event:" + event);
    }
}
