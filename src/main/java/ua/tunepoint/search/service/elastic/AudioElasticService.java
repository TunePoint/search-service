package ua.tunepoint.search.service.elastic;

import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;
import ua.tunepoint.search.config.Indices;
import ua.tunepoint.search.document.Audio;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AudioElasticService {

    private final ElasticsearchRestTemplate template;

    public void save(Audio audio) {
        template.save(audio);
    }

    public void update(Long id, String title, String description, String authorPseudonym, Boolean isPrivate) {
        Map<String, Object> params = new HashMap<>() {{
            put("title", title);
            put("description", description);
            put("author_pseudonym", authorPseudonym);
            put("is_private", isPrivate);
        }};

        UpdateQuery query = UpdateQuery.builder(String.valueOf(id))
                .withScript("""
                        ctx._source.title = params.title;
                        ctx._source.description = params.description;
                        ctx._source.author_pseudonym = params.author_pseudonym;
                        ctx._source.is_private = params.is_private;
                        """)
                .withParams(params)
                .build();

        template.update(query, IndexCoordinates.of(Indices.AUDIO_INDEX));
    }

    public void delete(Long id) {
        template.delete(
                String.valueOf(id),
                IndexCoordinates.of(Indices.AUDIO_INDEX)
        );
    }
}
