package ua.tunepoint.search.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import ua.tunepoint.search.service.client.FeignMarker;

@Configuration
@EnableFeignClients(basePackageClasses = FeignMarker.class)
@EnableConfigurationProperties
public class MainConfiguration {
}
