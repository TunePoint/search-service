package ua.tunepoint.search.document.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import ua.tunepoint.search.document.Playlist;

public interface PlaylistRepository extends ElasticsearchRepository<Playlist, Long> {
}
