package de.pheru.media.gui;

import de.pheru.fx.util.properties.ObservablePropertyKey;

public final class Settings {

    public static final ObservablePropertyKey<Boolean> APPLICATIONWINDOW_MAXIMIZED = new ObservablePropertyKey<>("applicationwindowMaximized", true);
    public static final ObservablePropertyKey<Double> APPLICATIONWINDOW_WIDTH = new ObservablePropertyKey<>("applicationwindowWidth", 600.0);
    public static final ObservablePropertyKey<Double> APPLICATIONWINDOW_HEIGHT = new ObservablePropertyKey<>("applicationwindowHeight", 600.0);
    public static final ObservablePropertyKey<Double> APPLICATIONWINDOW_SPLITPOSIITON = new ObservablePropertyKey<>("applicationwindowSplitposition", 0.0);

    public static final ObservablePropertyKey<String> MUSIC_DIRECTORY = new ObservablePropertyKey<>("musicDirectory", "");
    public static final ObservablePropertyKey<String> PLAYLIST_DIRECTORY = new ObservablePropertyKey<>("playlistsDirectory", "");

    public static final ObservablePropertyKey<Boolean> DONT_SHOW_AGAIN_CLOSE_APPLICATION_DIALOG = new ObservablePropertyKey<>("dontShowAgainCloseApplicationDialog", false);
    public static final ObservablePropertyKey<Boolean> SHORTCUTS_ENABLED = new ObservablePropertyKey<>("shortcutsEnabled", true);

    //    NOTIFICATIONS_POSITION("notificationsPosition", defaultValue), //TODO
    public static final ObservablePropertyKey<Integer> NOTIFICATIONS_DURATION = new ObservablePropertyKey<>("notificationsDuration", 5);

    public static final ObservablePropertyKey<Double> MUSICPLAYER_VOLUME = new ObservablePropertyKey<>("musicplayerVolume", 100.0);
    public static final ObservablePropertyKey<Boolean> MUSICPLAYER_MUTED = new ObservablePropertyKey<>("musicplayerMuted", false);
    public static final ObservablePropertyKey<Boolean> MUSICPLAYER_REPEAT = new ObservablePropertyKey<>("musicplayerRepeat", false);
    public static final ObservablePropertyKey<Boolean> MUSICPLAYER_RANDOM = new ObservablePropertyKey<>("musicplayerRandom", false);

    public static final ObservablePropertyKey<Boolean> EDITFILE_SORT_TITLES = new ObservablePropertyKey<>("editfileSortTitles", false);
    public static final ObservablePropertyKey<Boolean> EDITFILE_SORT_ALBUMS = new ObservablePropertyKey<>("editfileSortAlbums", false);
    public static final ObservablePropertyKey<Boolean> EDITFILE_SORT_ARTISTS = new ObservablePropertyKey<>("editfileSortArtists", false);
    public static final ObservablePropertyKey<Boolean> EDITFILE_SYNCHRONIZE_TITLES = new ObservablePropertyKey<>("editfileSynchronizeTitle", false);
}
