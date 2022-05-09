package ua.tunepoint.search.service.composite;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.tunepoint.audio.model.response.payload.AudioPayload;
import ua.tunepoint.search.api.model.ElasticScroll;
import ua.tunepoint.search.document.Audio;
import ua.tunepoint.search.service.domain.AudioDomainService;
import ua.tunepoint.search.service.search.AudioSearchService;

import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class AudioSearchFacade {

    private final AudioSearchService audioSearchService;
    private final AudioDomainService audioDomainService;

    public ElasticScroll<AudioPayload> searchAudio(String queryString, Integer pageSize) {
        var scroll = audioSearchService.search(queryString, pageSize);
        return transformDomain(scroll);
    }

    public ElasticScroll<AudioPayload> searchAudio(String scrollId) {
        var scroll = audioSearchService.search(scrollId);
        return transformDomain(scroll);
    }

    private ElasticScroll<AudioPayload> transformDomain(ElasticScroll<Audio> scroll) {
        if (scroll.isEmpty()) {
            return scroll.update(emptyList());
        }
        var audios = audioDomainService.bulk(
                scroll.getContent().stream().map(Audio::getId).collect(Collectors.toList())
        );
        return scroll.update(audios);
    }
}
