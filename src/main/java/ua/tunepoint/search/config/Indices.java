package ua.tunepoint.search.config;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Indices {

    public static final String USER_INDEX = "users";
    public static final String AUDIO_INDEX = "audio";
    public static final String PLAYLIST_INDEX = "playlists";

    public static final String FOLLOW_EVENT_INDEX = "follow-events-%s";
    public static final String LISTEN_EVENT_INDEX = "listen-events-%s";
    public static final String AUDIO_LIKE_EVENT_INDEX = "audio-like-events-%s";
    public static final String PLAYLIST_LIKE_EVENT_INDEX = "playlist-like-events-%s";
}
