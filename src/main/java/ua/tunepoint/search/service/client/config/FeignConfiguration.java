package ua.tunepoint.search.service.client.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import ua.tunepoint.security.UserContextRequestInterceptor;

public class FeignConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new UserContextRequestInterceptor();
    }
}
