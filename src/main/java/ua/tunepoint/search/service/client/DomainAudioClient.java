package ua.tunepoint.search.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import ua.tunepoint.audio.api.AudioEndpoint;

@FeignClient(name = "audio-service")
public interface DomainAudioClient extends AudioEndpoint {
}
