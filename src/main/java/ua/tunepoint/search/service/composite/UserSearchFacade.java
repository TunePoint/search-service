package ua.tunepoint.search.service.composite;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.tunepoint.account.model.response.payload.UserPublicPayload;
import ua.tunepoint.search.api.model.ElasticScroll;
import ua.tunepoint.search.document.User;
import ua.tunepoint.search.service.domain.UserDomainService;
import ua.tunepoint.search.service.search.UserSearchService;

import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class UserSearchFacade {

    private final UserSearchService userSearchService;
    private final UserDomainService userDomainService;

    public ElasticScroll<UserPublicPayload> searchUsers(String queryString, Integer pageSize) {
        var scroll = userSearchService.searchUser(queryString, pageSize);
        return transformDomain(scroll);
    }

    public ElasticScroll<UserPublicPayload> searchUsers(String scrollId) {
        return transformDomain(userSearchService.search(scrollId));
    }

    private ElasticScroll<UserPublicPayload> transformDomain(ElasticScroll<User> scroll) {
        if (scroll.isEmpty()) {
            return scroll.update(emptyList());
        }
        var users = userDomainService.bulk(
                scroll.getContent().stream().map(User::getId).collect(Collectors.toList())
        );
        return scroll.update(users);
    }
}
