package ua.tunepoint.search.config;

import lombok.SneakyThrows;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.PutIndexTemplateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.web.bind.annotation.PostMapping;
import ua.tunepoint.search.config.props.ElasticProperties;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Configuration
@EnableElasticsearchRepositories
public class ElasticConfiguration
        extends AbstractElasticsearchConfiguration
        implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private ElasticProperties elasticProps;

    @Bean
    @Override
    public RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration configuration = ClientConfiguration.builder()
                .connectedTo(elasticProps.getUrl())
                .build();

        return RestClients.create(configuration).rest();
    }

    @Override
    @SneakyThrows
    public void onApplicationEvent(ApplicationReadyEvent event) {

        var client = elasticsearchClient();

        for (var template : elasticProps.getTemplates()) { // TODO: change to composable index template
            var mappingsString = loadResource(template.getMappingPath());

            PutIndexTemplateRequest request = new PutIndexTemplateRequest(template.getName())
                    .patterns(template.getPatterns())
                    .mapping(mappingsString, XContentType.JSON)
                    .create(false);

            client.indices().putTemplate(request, RequestOptions.DEFAULT);
        }
    }

    @SneakyThrows
    private String loadResource(String path) {
        return new String(
                Objects.requireNonNull(
                                this.getClass().getClassLoader().getResourceAsStream(path)
                        )
                        .readAllBytes()
        );
    }

//    @PostConstruct
//    public void initMappings(ElasticsearchRestTemplate template) {
//        template.indexOps()
//    }
}
