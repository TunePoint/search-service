package ua.tunepoint.search.service.composite;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.tunepoint.audio.model.response.payload.PlaylistPayload;
import ua.tunepoint.search.document.Playlist;
import ua.tunepoint.search.service.domain.PlaylistDomainService;
import ua.tunepoint.search.service.search.PlaylistSearchService;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompositePlaylistService {

    private final PlaylistSearchService playlistSearchService;
    private final PlaylistDomainService playlistDomainService;

    public Page<PlaylistPayload> search(String queryString, Pageable pageable) {
        var elasticRecords = playlistSearchService.search(queryString, pageable);

        var playlists = playlistDomainService.bulk(
                elasticRecords.stream().map(Playlist::getId)
                        .collect(Collectors.toList())
        );

        return new PageImpl<>(playlists, pageable, elasticRecords.getTotalElements());
    }
}
