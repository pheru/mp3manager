package de.pheru.media.cdi.producers;

import de.pheru.media.cdi.qualifiers.XMLSettings;
import de.pheru.media.settings.Settings;
import de.pheru.media.settings.SettingsLoader;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 *
 * @author Philipp Bruckner
 */
@ApplicationScoped
public class SettingsProducer {

    @Inject
    private SettingsLoader loader;

    @Produces
    @XMLSettings
    @ApplicationScoped
    public Settings createSettings(){
        return loader.load();
    }
}
