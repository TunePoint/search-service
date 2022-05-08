package ua.tunepoint.search.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ua.tunepoint.account.model.response.payload.UserPublicPayload;
import ua.tunepoint.audio.model.response.payload.AudioPayload;
import ua.tunepoint.audio.model.response.payload.PlaylistPayload;
import ua.tunepoint.search.api.model.AggregatedResponse;
import ua.tunepoint.search.service.composite.CompositeAudioService;
import ua.tunepoint.search.service.composite.CompositePlaylistService;
import ua.tunepoint.search.service.composite.CompositeUserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/search")
public class SearchController {

    private final CompositeUserService userService;
    private final CompositePlaylistService playlistService;
    private final CompositeAudioService audioService;

    @GetMapping
    public AggregatedResponse search(@RequestParam("q") String queryString, @PageableDefault Pageable pageable) {
        return Mono.zip(
                        Mono.fromSupplier(() -> userService.searchUser(queryString, pageable)),
                        Mono.fromSupplier(() -> audioService.searchAudio(queryString, pageable)),
                        Mono.fromSupplier(() -> playlistService.search(queryString, pageable))
                )
                .map(
                        it -> new AggregatedResponse(
                                it.getT1(), it.getT3(), it.getT2()
                        )
                )
                .subscribeOn(Schedulers.boundedElastic()).block();
    }

    @GetMapping("/audio")
    public Page<AudioPayload> searchAudio(@PageableDefault Pageable pageable, @RequestParam("q") String queryString) {
        return audioService.searchAudio(queryString, pageable);
    }

    @GetMapping("/playlists")
    public Page<PlaylistPayload> searchPlaylists(@RequestParam("q") String queryString, @PageableDefault Pageable pageable) {
        return playlistService.search(queryString, pageable);
    }

    @GetMapping("/users")
    public Page<UserPublicPayload> searchUser(@PageableDefault Pageable pageable, @RequestParam("q") String queryString) {
        return userService.searchUser(queryString, pageable);
    }
}
