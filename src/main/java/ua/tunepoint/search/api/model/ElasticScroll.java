package ua.tunepoint.search.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchScrollHits;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ElasticScroll<T> {

    private String scrollId;
    private List<T> content;
    private Long total;

    public boolean isEmpty() {
        return content == null || content.isEmpty();
    }

    public ElasticScroll(SearchScrollHits<T> scroll) {
        this.scrollId = scroll.getScrollId();
        this.content = scroll.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
        this.total = scroll.getTotalHits();
    }

    public <V> ElasticScroll<V> update(List<V> newContent) {
        return new ElasticScroll<>(
                this.scrollId,
                newContent,
                this.total
        );
    }
}
