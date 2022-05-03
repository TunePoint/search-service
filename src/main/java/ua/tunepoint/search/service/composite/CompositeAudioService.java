package ua.tunepoint.search.service.composite;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.tunepoint.audio.model.response.payload.AudioPayload;
import ua.tunepoint.search.document.Audio;
import ua.tunepoint.search.service.domain.AudioDomainService;
import ua.tunepoint.search.service.search.AudioSearchService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompositeAudioService {

    private final AudioSearchService audioSearchService;
    private final AudioDomainService audioDomainService;

    public List<AudioPayload> searchAudio(String queryString, Pageable pageable) {
        var elasticRecords = audioSearchService.search(queryString, pageable);

        return audioDomainService.bulk(
                elasticRecords.stream().map(Audio::getId)
                        .collect(Collectors.toList())
        );
    }
}
