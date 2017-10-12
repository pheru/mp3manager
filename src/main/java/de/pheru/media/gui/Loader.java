package de.pheru.media.gui;

import de.pheru.fx.mvp.PheruFXLoader;
import de.pheru.fx.util.properties.ObservableProperties;
import de.pheru.media.util.GlobalKeyListener;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import javax.inject.Inject;

public class Loader extends PheruFXLoader {

    private static final Logger LOGGER = LogManager.getLogger(Loader.class);

    @Inject
    private ObservableProperties settings;
    @Inject
    private GlobalKeyListener globalKeyListener;

    @Override
    public void load() throws Exception {
        updateMessage("Lade Einstellungen...");
        updateProgress(0, 100);
        if (settings.booleanProperty(Settings.SHORTCUTS_ENABLED).get()) {
            initJNativeHook();
        }
        initNotifications();
        updateProgress(100, 100);
    }

    private void initJNativeHook() {
        try {
            GlobalScreen.addNativeKeyListener(globalKeyListener);
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException | UnsatisfiedLinkError e) {
            LOGGER.error("Exception/Error initializing JNativeHook!", e);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.getDialogPane().setPrefWidth(650.0);
            alert.setHeaderText("Shortcuts konnten nicht initialisiert werden!");
            alert.setContentText("Shortcuts werden deaktiviert.");
            alert.showAndWait();
            settings.booleanProperty(Settings.SHORTCUTS_ENABLED).set(false);
        }
    }

    private void initNotifications() {
        //TODO
//        Notification.getDefaults().positionProperty().bind(settings.notificationsPositionProperty());
//        Notifications.defaultDurationProperty().bind(settings.notificationsDurationProperty());
    }
}
