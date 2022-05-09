package ua.tunepoint.search.service.event.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.tunepoint.audio.model.event.audio.AudioCreatedEvent;
import ua.tunepoint.audio.model.event.audio.AudioDeletedEvent;
import ua.tunepoint.audio.model.event.audio.AudioLikeEvent;
import ua.tunepoint.audio.model.event.audio.AudioListenEvent;
import ua.tunepoint.audio.model.event.audio.AudioUnlikeEvent;
import ua.tunepoint.audio.model.event.audio.AudioUpdatedEvent;
import ua.tunepoint.event.model.DomainEvent;
import ua.tunepoint.search.config.Indices;
import ua.tunepoint.search.document.Audio;
import ua.tunepoint.search.document.event.ListenEvent;
import ua.tunepoint.search.service.ElasticService;
import ua.tunepoint.search.service.TimeIndexProvider;
import ua.tunepoint.search.service.elastic.AudioElasticService;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AudioEventHandler {

    private final ElasticService elasticService;
    private final AudioElasticService audioElasticService;
    private final TimeIndexProvider indexProvider;

    public void handleAudioCreated(AudioCreatedEvent event) {
        Audio audio = new Audio();
        audio.setId(event.getAudioId());
        audio.setOwnerId(event.getAudioOwnerId());
        audio.setIsPrivate(event.getIsPrivate());
        audio.setAuthorPseudonym(event.getAuthorPseudonym());
        audio.setTitle(event.getTitle());
        audio.setDescription(event.getDescription());

        audioElasticService.save(audio);
    }

    public void handleAudioUpdated(AudioUpdatedEvent event) {
        log(event);
        audioElasticService.update(
                event.getAudioId(),
                event.getTitle(),
                event.getDescription(),
                event.getAuthorPseudonym(),
                event.getIsPrivate()
        );
    }

    public void handleAudioLike(AudioLikeEvent event) {
        log(event);
        elasticService.index(
                indexProvider.current(Indices.AUDIO_LIKE_EVENT_INDEX, LocalDateTime.now()),
                new ua.tunepoint.search.document.event.AudioLikeEvent(
                        event.getAudioId(),
                        event.getAudioOwnerId(),
                        event.getUserId(),
                        1
                )
        );
    }

    public void handleAudioDeleted(AudioDeletedEvent event) {
        log(event);
        audioElasticService.delete(event.getAudioId());
    }

    public void handleAudioUnlike(AudioUnlikeEvent event) {
        log(event);
        elasticService.index(
                indexProvider.current(Indices.AUDIO_LIKE_EVENT_INDEX, LocalDateTime.now()),
                new ua.tunepoint.search.document.event.AudioLikeEvent(
                        event.getAudioId(),
                        event.getAudioOwnerId(),
                        event.getUserId(),
                        -1
                )
        );
    }

    public void handleAudioListen(AudioListenEvent event) {
        log(event);
        elasticService.index(
                indexProvider.current(Indices.LISTEN_EVENT_INDEX, LocalDateTime.now()),
                new ListenEvent(
                        event.getAudioId(),
                        event.getAudioOwnerId(),
                        event.getUserId()
                )
        );
    }

    private void log(DomainEvent event) {
        log.info("Processing event:" + event);
    }
}
