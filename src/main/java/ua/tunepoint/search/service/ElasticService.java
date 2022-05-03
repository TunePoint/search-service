package ua.tunepoint.search.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ElasticService {

    private final RestHighLevelClient client;
    private final ObjectMapper mapper;

    public IndexResponse index(String index, Object document) {
        return index(index, null, document);
    }

    public IndexResponse index(String index, String id, Object document) {
        IndexRequest request = new IndexRequest(index);
        request.id(id);
        request.source(toJson(document), XContentType.JSON);

        try {
            return client.index(request, RequestOptions.DEFAULT);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
