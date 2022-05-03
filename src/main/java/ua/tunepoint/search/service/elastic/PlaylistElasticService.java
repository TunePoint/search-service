package ua.tunepoint.search.service.elastic;

import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;
import ua.tunepoint.search.config.Indices;
import ua.tunepoint.search.document.Playlist;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlaylistElasticService {

    private final ElasticsearchRestTemplate template;

    public void save(Playlist playlist) {
        template.save(playlist);
    }

    public void update(Long id, String title, String description, Boolean isPrivate) {
        var params = Map.<String, Object>of(
                "title", title,
                "description", description,
                "is_private", isPrivate
        );

        var updateQuery = UpdateQuery.builder(String.valueOf(id))
                .withScript("""
                        ctx._source.title = params.title;
                        ctx._source.description = params.description;
                        ctx._source.is_private = params.is_private;
                        """)
                .withParams(params)
                .build();

        template.update(
                updateQuery,
                IndexCoordinates.of(Indices.PLAYLIST_INDEX)
        );
    }

    public void delete(Long id) {
        template.delete(
                String.valueOf(id),
                IndexCoordinates.of(Indices.PLAYLIST_INDEX)
        );
    }
}
