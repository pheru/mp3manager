package de.pheru.media.cdi.producers;

import de.pheru.media.cdi.qualifiers.XMLSettings;
import de.pheru.media.settings.Settings;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 *
 * @author Philipp Bruckner
 */
@ApplicationScoped
public class SettingsProducer {
    
    @Produces
    @XMLSettings
    @ApplicationScoped
    public Settings createSettings(){
        return Settings.load();
    }
}
