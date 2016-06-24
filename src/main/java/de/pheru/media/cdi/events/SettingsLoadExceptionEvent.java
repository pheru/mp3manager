package de.pheru.media.cdi.events;

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
