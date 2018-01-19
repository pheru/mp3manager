package de.pheru.media.desktop.data;

import java.util.ArrayList;
import java.util.List;

public class AudioLibrary {

    private String name = "Neue Bibliothek";
    private List<String> directories = new ArrayList<>();
    private long lastUpdated = 0;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<String> getDirectories() {
        return directories;
    }

    public void setDirectories(final List<String> directories) {
        this.directories = directories;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(final long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
