package ua.tunepoint.search.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.tunepoint.account.model.response.payload.UserPublicPayload;
import ua.tunepoint.search.api.model.AudioSearchResponse;
import ua.tunepoint.search.api.model.PlaylistSearchResponse;
import ua.tunepoint.search.api.model.UserSearchResponse;
import ua.tunepoint.search.service.composite.AudioSearchFacade;
import ua.tunepoint.search.service.composite.PlaylistSearchFacade;
import ua.tunepoint.search.service.composite.UserSearchFacade;

@RequiredArgsConstructor
@RestController
@RequestMapping("/search")
public class SearchController {

    private final UserSearchFacade userService;
    private final PlaylistSearchFacade playlistService;
    private final AudioSearchFacade audioService;

    @GetMapping("/audio")
    public AudioSearchResponse searchAudio(@RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, @RequestParam("q") String queryString) {
        return AudioSearchResponse.builder()
                .payload(audioService.searchAudio(queryString, pageSize))
                .build();
    }

    @GetMapping("/audio/{scrollId}")
    public AudioSearchResponse searchAudioWithScrollId(@PathVariable String scrollId) {
        return AudioSearchResponse.builder()
                .payload(audioService.searchAudio(scrollId))
                .build();
    }

    @GetMapping("/playlists")
    public PlaylistSearchResponse searchPlaylists(@RequestParam("q") String queryString, @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return PlaylistSearchResponse.builder()
                .payload(playlistService.search(queryString, pageSize))
                .build();
    }

    @GetMapping("/playlists/{scrollId}")
    public PlaylistSearchResponse searchPlaylistsByScrollId(@PathVariable String scrollId) {
        return PlaylistSearchResponse.builder()
                .payload(playlistService.search(scrollId))
                .build();
    }

    @GetMapping("/users")
    public UserSearchResponse searchUser(@RequestParam("q") String queryString, @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return UserSearchResponse.builder()
                .payload(userService.searchUsers(queryString, pageSize))
                .build();
    }

    @GetMapping("/users/{scrollId}")
    public UserSearchResponse scrollUsers(@PathVariable String scrollId) {
        return UserSearchResponse.builder()
                .payload(userService.searchUsers(scrollId))
                .build();
    }
}
