package ua.tunepoint.search.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import ua.tunepoint.account.api.UserEndpoint;

@FeignClient(name = "user-service")
public interface DomainUserClient extends UserEndpoint {
}
