package ua.tunepoint.search.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import ua.tunepoint.account.api.UserEndpoint;
import ua.tunepoint.search.service.client.config.FeignConfiguration;

@FeignClient(name = "account-service", configuration = FeignConfiguration.class)
public interface DomainUserClient extends UserEndpoint {
}
