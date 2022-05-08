package ua.tunepoint.search.service.composite;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.tunepoint.account.model.response.payload.UserPublicPayload;
import ua.tunepoint.search.document.User;
import ua.tunepoint.search.service.domain.UserDomainService;
import ua.tunepoint.search.service.search.UserSearchService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompositeUserService {

    private final UserSearchService userSearchService;
    private final UserDomainService userDomainService;

    public Page<UserPublicPayload> searchUser(String queryString, Pageable pageable) {
        var elasticRecords = userSearchService.searchUser(queryString, pageable);

        var users = userDomainService.bulk(
                elasticRecords.stream().map(User::getId)
                        .collect(Collectors.toList())
        );

        return new PageImpl<>(users, pageable, elasticRecords.getTotalElements());
    }
}
