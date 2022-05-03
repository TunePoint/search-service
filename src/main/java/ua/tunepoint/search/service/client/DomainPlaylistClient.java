package ua.tunepoint.search.service.client;


import org.springframework.cloud.openfeign.FeignClient;
import ua.tunepoint.audio.api.PlaylistEndpoint;

@FeignClient(name = "playlist-service", url = "http://audio-service")
public interface DomainPlaylistClient extends PlaylistEndpoint {
}
