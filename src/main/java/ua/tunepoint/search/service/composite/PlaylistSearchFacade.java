package ua.tunepoint.search.service.composite;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.tunepoint.audio.model.response.payload.PlaylistPayload;
import ua.tunepoint.search.api.model.ElasticScroll;
import ua.tunepoint.search.document.Playlist;
import ua.tunepoint.search.service.domain.PlaylistDomainService;
import ua.tunepoint.search.service.search.PlaylistSearchService;

import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class PlaylistSearchFacade {

    private final PlaylistSearchService playlistSearchService;
    private final PlaylistDomainService playlistDomainService;

    public ElasticScroll<PlaylistPayload> search(String queryString, Integer pageSize) {
        var scroll = playlistSearchService.search(queryString, pageSize);
        return transformDomain(scroll);
    }

    public ElasticScroll<PlaylistPayload> search(String scrollId) {
        return transformDomain(playlistSearchService.search(scrollId));
    }

    private ElasticScroll<PlaylistPayload> transformDomain(ElasticScroll<Playlist> scroll) {
        if (scroll.isEmpty()) {
            return scroll.update(emptyList());
        }
        var audios = playlistDomainService.bulk(
                scroll.getContent().stream().map(Playlist::getId).collect(Collectors.toList())
        );
        return scroll.update(audios);
    }
}
