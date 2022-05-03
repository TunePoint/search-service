package ua.tunepoint.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.tunepoint.search.config.props.AggregationProperties;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class TimeIndexProvider {

    private final Duration timeWindow;

    public TimeIndexProvider(AggregationProperties props) {
        this.timeWindow = props.getWindow();
    }

    public String current(String pattern, LocalDateTime now) {
        return String.format(
                pattern,
                currentWindow(now)
        );
    }

    public String previous(String pattern, LocalDateTime now) {
        return String.format(
                pattern,
                currentWindow(now) - 1
        );
    }

    private Long currentWindow(LocalDateTime now) {
        var epochMillis = now.toInstant(ZoneOffset.UTC).toEpochMilli();
        var windowMillis = timeWindow.toMillis();

        return epochMillis / windowMillis;
    }
}
