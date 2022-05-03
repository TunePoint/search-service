package ua.tunepoint.search.service.composite;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.tunepoint.audio.model.response.payload.PlaylistPayload;
import ua.tunepoint.search.document.Playlist;
import ua.tunepoint.search.service.domain.PlaylistDomainService;
import ua.tunepoint.search.service.search.PlaylistSearchService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompositePlaylistService {

    private final PlaylistSearchService playlistSearchService;
    private final PlaylistDomainService playlistDomainService;

    public List<PlaylistPayload> search(String queryString, Pageable pageable) {
        var elasticRecords = playlistSearchService.search(queryString, pageable);

        return playlistDomainService.bulk(
                elasticRecords.stream().map(Playlist::getId)
                        .collect(Collectors.toList())
        );
    }
}
