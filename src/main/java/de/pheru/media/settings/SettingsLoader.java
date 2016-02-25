package de.pheru.media.settings;

import de.pheru.media.cdi.events.SettingsLoadExceptionEvent;
import de.pheru.media.cdi.events.SettingsNotFoundEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * Created by Philipp on 12.01.2016.
 */
@ApplicationScoped
public class SettingsLoader {

    private static final Logger LOGGER = LogManager.getLogger(SettingsLoader.class);

    @Inject
    private Event<SettingsLoadExceptionEvent> settingsLoadExceptionEvent;
    @Inject
    private Event<SettingsNotFoundEvent> settingsNotFoundEvent;

    public Settings load() {
        try {
            if (!new File(Settings.FILE_PATH).exists()) {
                LOGGER.info("Keine Einstellungen gefunden; neue werden beim Schließen der Anwendung angelegt.");
                settingsNotFoundEvent.fire(new SettingsNotFoundEvent());
                return createDefaultSettings();
            }
            JAXBContext context = JAXBContext.newInstance(Settings.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setEventHandler(event -> {
                LOGGER.error("Invalid settings.xml!");
                return false;
            });
            return (Settings) unmarshaller.unmarshal(new File(Settings.FILE_PATH));
        } catch (JAXBException e) {
            LOGGER.error("Exception parsing settings.xml!", e);
            boolean invalidSettingsFilePreserved = preserveInvalidSettingsFile();
            settingsLoadExceptionEvent.fire(new SettingsLoadExceptionEvent(invalidSettingsFilePreserved));
            return createDefaultSettings();
        }
    }

    private boolean preserveInvalidSettingsFile() {
        File oldInvalidSettingsFile = new File(Settings.FILE_PATH.replace(".xml", "") + "_INVALID.xml");
        if (!oldInvalidSettingsFile.exists() || oldInvalidSettingsFile.delete()) {
            File currentInvalidSettingsFile = new File(Settings.FILE_PATH);
            return currentInvalidSettingsFile.renameTo(oldInvalidSettingsFile);
        }
        return false;
    }

    private Settings createDefaultSettings() {
        Settings defaultSettings = new Settings();
        defaultSettings.initDefaultMainColumnSettings();
        return defaultSettings;
    }

}
