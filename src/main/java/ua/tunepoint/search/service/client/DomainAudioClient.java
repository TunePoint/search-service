package ua.tunepoint.search.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import ua.tunepoint.audio.api.AudioEndpoint;
import ua.tunepoint.search.service.client.config.FeignConfiguration;

@FeignClient(name = "audio-service", configuration = FeignConfiguration.class)
public interface DomainAudioClient extends AudioEndpoint {
}
