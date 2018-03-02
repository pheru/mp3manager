package de.pheru.media.desktop;

import de.pheru.fx.util.properties.ObservablePropertyKey;
import de.pheru.media.desktop.data.AudioLibrary;

public final class Setting {

    public static final ObservablePropertyKey<Boolean> APPLICATIONWINDOW_MAXIMIZED = new ObservablePropertyKey<>("applicationwindowMaximized", true);
    public static final ObservablePropertyKey<Double> APPLICATIONWINDOW_WIDTH = new ObservablePropertyKey<>("applicationwindowWidth", 800.0);
    public static final ObservablePropertyKey<Double> APPLICATIONWINDOW_HEIGHT = new ObservablePropertyKey<>("applicationwindowHeight", 600.0);
    public static final ObservablePropertyKey<Double> APPLICATIONWINDOW_SPLITPOSIITON = new ObservablePropertyKey<>("applicationwindowSplitposition", 0.0);

    public static final ObservablePropertyKey<String> CURRENT_AUDIO_LIBRARY_FILENAME = new ObservablePropertyKey<>("currentAudioLibrary", AudioLibrary.DEFAULT_FILENAME);
    public static final ObservablePropertyKey<String> PLAYLIST_DIRECTORY = new ObservablePropertyKey<>("playlistsDirectory", "");

    public static final ObservablePropertyKey<Double> MUSICPLAYER_VOLUME = new ObservablePropertyKey<>("musicplayerVolume", 100.0);
    public static final ObservablePropertyKey<Boolean> MUSICPLAYER_MUTED = new ObservablePropertyKey<>("musicplayerMuted", false);
    public static final ObservablePropertyKey<Boolean> MUSICPLAYER_REPEAT = new ObservablePropertyKey<>("musicplayerRepeat", false);
    public static final ObservablePropertyKey<Boolean> MUSICPLAYER_RANDOM = new ObservablePropertyKey<>("musicplayerRandom", false);

    private Setting() {
        //Utility-Klasse
    }
}
