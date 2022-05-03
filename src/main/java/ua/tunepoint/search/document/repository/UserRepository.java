package ua.tunepoint.search.document.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import ua.tunepoint.search.document.User;

public interface UserRepository extends ElasticsearchRepository<User, Long> {
}
