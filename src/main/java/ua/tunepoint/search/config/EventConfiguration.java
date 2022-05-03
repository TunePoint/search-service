package ua.tunepoint.search.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.tunepoint.account.model.event.ProfileEventType;
import ua.tunepoint.account.model.event.UserEventType;
import ua.tunepoint.audio.model.event.AudioEventType;
import ua.tunepoint.audio.model.event.PlaylistEventType;
import ua.tunepoint.auth.model.event.AuthEventType;
import ua.tunepoint.event.starter.DomainRelation;
import ua.tunepoint.event.starter.handler.DomainEventHandlers;
import ua.tunepoint.event.starter.registry.DomainRegistry;
import ua.tunepoint.event.starter.registry.builder.DomainRegistryBuilder;
import ua.tunepoint.search.service.event.DomainEventService;

import java.util.Set;

import static ua.tunepoint.account.model.event.AccountDomain.PROFILE;
import static ua.tunepoint.account.model.event.AccountDomain.USER;
import static ua.tunepoint.audio.model.event.Domain.AUDIO;
import static ua.tunepoint.audio.model.event.Domain.PLAYLIST;
import static ua.tunepoint.auth.model.event.AuthDomain.AUTH;

@Configuration
public class EventConfiguration {

    @Bean
    public DomainRegistry domainRegistry() {
        return new DomainRegistryBuilder()
                .register(
                        USER.getName(),
                        UserEventType.values(),
                        Set.of(DomainRelation.CONSUMER)
                )
                .register(
                        PROFILE.getName(),
                        ProfileEventType.values(),
                        Set.of(DomainRelation.CONSUMER)
                )
                .register(
                        AUDIO.getName(),
                        AudioEventType.values(),
                        Set.of(DomainRelation.CONSUMER)
                )
                .register(
                        AUTH.getName(),
                        AuthEventType.values(),
                        Set.of(DomainRelation.CONSUMER)
                )
                .register(
                        PLAYLIST.getName(),
                        PlaylistEventType.values(),
                        Set.of(DomainRelation.CONSUMER)
                )
                .build();
    }

    @Bean
    public DomainEventHandlers domainEventHandlers(DomainEventService eventService) {
        return eventService.eventHandlers();
    }
}
