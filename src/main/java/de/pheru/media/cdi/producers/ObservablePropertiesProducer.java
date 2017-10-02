package de.pheru.media.cdi.producers;

import de.pheru.fx.util.properties.ObservableProperties;
import de.pheru.media.cdi.events.SettingsLoadExceptionEvent;
import de.pheru.media.gui.PheruMedia;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.IOException;

@ApplicationScoped
public class ObservablePropertiesProducer {

    private static final Logger LOGGER = LogManager.getLogger(ObservablePropertiesProducer.class);

    public static final String FILENAME = "settings.properties";

    @Inject
    private Event<SettingsLoadExceptionEvent> settingsLoadExceptionEvent;

    @Produces
    @ApplicationScoped
    public ObservableProperties createObservableProperties() {
        final ObservableProperties properties = new ObservableProperties(PheruMedia.APPLICATION_PATH + "/" + FILENAME);
        try {
            properties.load();
        } catch (final IOException e) {
            if (e instanceof FileNotFoundException) {
                LOGGER.info("Settings not found.");
            } else {
                settingsLoadExceptionEvent.fire(new SettingsLoadExceptionEvent(false));
            }
        }
        return properties;
    }
}
