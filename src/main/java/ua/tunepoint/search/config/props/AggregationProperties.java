package ua.tunepoint.search.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "aggregation")
public class AggregationProperties {

    private Duration window;
}
