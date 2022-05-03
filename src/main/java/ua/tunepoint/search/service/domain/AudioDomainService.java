package ua.tunepoint.search.service.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.tunepoint.account.model.response.payload.UserPublicPayload;
import ua.tunepoint.audio.model.response.payload.AudioPayload;
import ua.tunepoint.search.service.client.DomainAudioClient;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AudioDomainService {

    private final DomainAudioClient client;

    public List<AudioPayload> bulk(List<Long> ids) {
        var response = client.searchBulk(ids);
        if (response == null || response.getBody() == null) {
            log.error("audio-service returned empty body");

            throw new RuntimeException();
        }

        var body = response.getBody();
        return body.getPayload();
    }
}
