package ua.tunepoint.search.api.model;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ua.tunepoint.account.model.response.payload.UserPublicPayload;
import ua.tunepoint.audio.model.response.payload.AudioPayload;
import ua.tunepoint.web.model.CommonResponse;

@SuperBuilder
@NoArgsConstructor
public class UserSearchResponse extends CommonResponse<ElasticScroll<UserPublicPayload>> {
}
