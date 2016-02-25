package de.pheru.media.cdi.events;

/**
 * Created by Philipp on 21.02.2016.
 */
public class SettingsLoadExceptionEvent {

    private boolean invalidSettingsFilePreserved;

    public SettingsLoadExceptionEvent(boolean invalidSettingsFilePreserved) {
        this.invalidSettingsFilePreserved = invalidSettingsFilePreserved;
    }

    public boolean isInvalidSettingsFilePreserved() {
        return invalidSettingsFilePreserved;
    }

    public void setInvalidSettingsFilePreserved(boolean invalidSettingsFilePreserved) {
        this.invalidSettingsFilePreserved = invalidSettingsFilePreserved;
    }
}
