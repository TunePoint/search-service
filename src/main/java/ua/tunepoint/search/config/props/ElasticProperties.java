package ua.tunepoint.search.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticProperties {

    private String url;

    private List<TemplateProperties> templates;

    @Data
    public static class TemplateProperties {

        private String name;
        private List<String> patterns;
        private String mappingPath;
    }
}
