package ua.tunepoint.search.service.client;


import org.springframework.cloud.openfeign.FeignClient;
import ua.tunepoint.audio.api.PlaylistEndpoint;
import ua.tunepoint.search.service.client.config.FeignConfiguration;

@FeignClient(name = "audio-service", contextId = "playlist-service", configuration = FeignConfiguration.class)
public interface DomainPlaylistClient extends PlaylistEndpoint {
}
