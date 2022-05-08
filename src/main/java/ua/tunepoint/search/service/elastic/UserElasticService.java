package ua.tunepoint.search.service.elastic;

import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;
import ua.tunepoint.search.config.Indices;
import ua.tunepoint.search.document.User;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserElasticService {

    private final ElasticsearchRestTemplate template;

    public void save(User user) {
         template.save(user);
    }

    public void update(Long id, String firstName, String lastName, String pseudonym, String bio) {

        Map<String, Object> params = new HashMap<>(){{
            put("first_name", firstName);
            put("last_name", lastName);
            put("pseudonym", pseudonym);
            put("bio", bio);
        }};

        UpdateQuery query = UpdateQuery.builder(String.valueOf(id))
                .withScript("""
                        ctx._source.first_name = params.first_name;
                        ctx._source.last_name = params.last_name;
                        ctx._source.pseudonym = params.pseudonym;
                        ctx._source.bio = params.bio;
                        """)
                .withParams(params)
                .build();

        template.update(query, IndexCoordinates.of(Indices.USER_INDEX));
    }

    public void delete(Long id) {
        template.delete(
                String.valueOf(id),
                IndexCoordinates.of(Indices.USER_INDEX)
        );
    }
}
