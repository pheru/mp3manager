package de.pheru.media.util;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GlobalKeyListener implements NativeKeyListener {

    private final List<GlobalKeyHandler> keyPressedHandlers = new ArrayList<>();

    public void addKeyPressedHandler(GlobalKeyHandler keyPressedHandler) {
        keyPressedHandlers.add(keyPressedHandler);
    }

    public void removeKeyPressedHandler(GlobalKeyHandler keyPressedHandler) {
        keyPressedHandlers.remove(keyPressedHandler);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        for (GlobalKeyHandler keyPressedHandler : keyPressedHandlers) {
            keyPressedHandler.handle(e);
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        //Nicht benötigt
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        //Nicht benötigt
    }

    public interface GlobalKeyHandler {

        void handle(NativeKeyEvent e);
    }
}
