package ua.tunepoint.search.document.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import ua.tunepoint.search.document.Audio;

public interface AudioRepository extends ElasticsearchRepository<Audio, Long> {
}
