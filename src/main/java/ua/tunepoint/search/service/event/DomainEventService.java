package ua.tunepoint.search.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.tunepoint.account.model.event.profile.ProfileUpdatedEvent;
import ua.tunepoint.account.model.event.user.UserFollowedEvent;
import ua.tunepoint.account.model.event.user.UserUnfollowedEvent;
import ua.tunepoint.audio.model.event.audio.AudioCreatedEvent;
import ua.tunepoint.audio.model.event.audio.AudioDeletedEvent;
import ua.tunepoint.audio.model.event.audio.AudioLikeEvent;
import ua.tunepoint.audio.model.event.audio.AudioListenEvent;
import ua.tunepoint.audio.model.event.audio.AudioUnlikeEvent;
import ua.tunepoint.audio.model.event.audio.AudioUpdatedEvent;
import ua.tunepoint.audio.model.event.playlist.PlaylistCreatedEvent;
import ua.tunepoint.audio.model.event.playlist.PlaylistDeletedEvent;
import ua.tunepoint.audio.model.event.playlist.PlaylistLikedEvent;
import ua.tunepoint.audio.model.event.playlist.PlaylistUnlikedEvent;
import ua.tunepoint.audio.model.event.playlist.PlaylistUpdatedEvent;
import ua.tunepoint.auth.model.event.user.UserRegisteredEvent;
import ua.tunepoint.event.starter.handler.DomainEventHandlers;
import ua.tunepoint.event.starter.handler.DomainEventHandlersBuilder;
import ua.tunepoint.event.starter.registry.DomainRegistry;
import ua.tunepoint.search.service.event.handler.AudioEventHandler;
import ua.tunepoint.search.service.event.handler.PlaylistEventHandler;
import ua.tunepoint.search.service.event.handler.UserEventHandler;

import static ua.tunepoint.account.model.event.AccountDomain.PROFILE;
import static ua.tunepoint.account.model.event.AccountDomain.USER;
import static ua.tunepoint.audio.model.event.Domain.AUDIO;
import static ua.tunepoint.audio.model.event.Domain.PLAYLIST;
import static ua.tunepoint.auth.model.event.AuthDomain.AUTH;

@Service
@RequiredArgsConstructor
public class DomainEventService {

    private final DomainRegistry domainRegistry;

    private final UserEventHandler userEventHandler;
    private final AudioEventHandler audioEventHandler;
    private final PlaylistEventHandler playlistEventHandler;

    public DomainEventHandlers eventHandlers() {
        return DomainEventHandlersBuilder.withRegistry(domainRegistry)
                .forDomain(AUDIO.getName())
                    .onEvent(AudioCreatedEvent.class, audioEventHandler::handleAudioCreated)
                    .onEvent(AudioUpdatedEvent.class, audioEventHandler::handleAudioUpdated)
                    .onEvent(AudioDeletedEvent.class, audioEventHandler::handleAudioDeleted)
                    .onEvent(AudioLikeEvent.class, audioEventHandler::handleAudioLike)
                    .onEvent(AudioUnlikeEvent.class, audioEventHandler::handleAudioUnlike)
                    .onEvent(AudioListenEvent.class, audioEventHandler::handleAudioListen)
                .forDomain(PLAYLIST.getName())
                    .onEvent(PlaylistLikedEvent.class, playlistEventHandler::handlePlaylistLiked)
                    .onEvent(PlaylistUnlikedEvent.class, playlistEventHandler::handlePlaylistUnliked)
                    .onEvent(PlaylistCreatedEvent.class, playlistEventHandler::handlePlaylistCreated)
                    .onEvent(PlaylistUpdatedEvent.class, playlistEventHandler::handlePlaylistUpdated)
                    .onEvent(PlaylistDeletedEvent.class, playlistEventHandler::handlePlaylistDeleted)
                .forDomain(AUTH.getName())
                    .onEvent(UserRegisteredEvent.class, userEventHandler::handleAuthRegistered)
                .forDomain(USER.getName())
                    .onEvent(UserFollowedEvent.class, userEventHandler::handleFollow)
                    .onEvent(UserUnfollowedEvent.class, userEventHandler::handleUnfollow)
                .forDomain(PROFILE.getName())
                    .onEvent(ProfileUpdatedEvent.class, userEventHandler::handleProfileUpdated)
                .build();
    }
}
