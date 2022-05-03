package ua.tunepoint.search.service.event.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.tunepoint.account.model.event.profile.ProfileUpdatedEvent;
import ua.tunepoint.account.model.event.user.UserFollowedEvent;
import ua.tunepoint.account.model.event.user.UserUnfollowedEvent;
import ua.tunepoint.auth.model.event.user.UserRegisteredEvent;
import ua.tunepoint.search.config.Indices;
import ua.tunepoint.search.document.User;
import ua.tunepoint.search.document.event.FollowEvent;
import ua.tunepoint.search.service.ElasticService;
import ua.tunepoint.search.service.TimeIndexProvider;
import ua.tunepoint.search.service.elastic.UserElasticService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserEventHandler {

    private final UserElasticService userElasticService;
    private final ElasticService elasticService;

    private final TimeIndexProvider indexProvider;

    public void handleProfileUpdated(ProfileUpdatedEvent event) {
        userElasticService.update(
                event.getId(),
                event.getFirstName(),
                event.getLastName(),
                event.getPseudonym(),
                event.getBio()
        );
    }

    public void handleFollow(UserFollowedEvent event) {
        elasticService.index(
                indexProvider.current(Indices.FOLLOW_EVENT_INDEX, LocalDateTime.now()),
                new FollowEvent(
                        event.getUserId(),
                        event.getFollowerId(),
                        1
                )
        );
    }

    public void handleUnfollow(UserUnfollowedEvent event) {
        elasticService.index(
                indexProvider.current(Indices.FOLLOW_EVENT_INDEX, LocalDateTime.now()),
                new FollowEvent(
                        event.getUserId(),
                        event.getFollowerId(),
                        -1
                )
        );
    }

    public void handleAuthRegistered(UserRegisteredEvent event) {
        var user = new User();
        user.setId(event.getUserId());
        user.setUsername(event.getUsername());

        userElasticService.save(user);
    }
}
