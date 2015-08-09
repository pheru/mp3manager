package de.eru.mp3manager;

import de.eru.mp3manager.utils.ExceptionHandler;
import java.awt.AWTException;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author Philipp Bruckner
 */
@ApplicationScoped
public class Mp3SystemTrayIcon {

    private SystemTray systemTray;
    private TrayIcon trayIcon;

    @PostConstruct
    private void init() {
        if (SystemTray.isSupported()) {
            systemTray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("img/trayIcon.png"));
            trayIcon = new TrayIcon(image, "Mp3Manager", new PopupMenu());
            trayIcon.setImageAutoSize(true);
            trayIcon.getPopupMenu().setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            try {
                systemTray.add(trayIcon);
            } catch (AWTException e) {
                //TODO Stattdessen direkt eine Runtime-Exception werfen?
                ExceptionHandler.handle(e, "TrayIcon konnte nicht initialisert werden! Die Anwendung kann nicht im SystemTray versteckt werden.",
                        "Exception initializing SystemTrayIcon");
                //TODO Anwendung darf nicht versteckt werden
            }
        }
    }

    public void shutdown() {
        systemTray.remove(trayIcon);
    }

    public void addPopUpMenuItem(String label, ActionListener actionListener) {
        if (SystemTray.isSupported()) {
            MenuItem menuItem = new MenuItem(label);
            menuItem.addActionListener(actionListener);
            trayIcon.getPopupMenu().add(menuItem);
        }
    }

    public void addOnClick(Runnable runnable) {
        if (SystemTray.isSupported()) {
            trayIcon.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    //Nicht benötigt
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    //Nicht benötigt
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        runnable.run();
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    //Nicht unterstützt
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    //Nicht unterstützt
                }
            });
        }
    }

    public void setImage(String image) {
        if (SystemTray.isSupported()) {
            trayIcon.setImage(Toolkit.getDefaultToolkit().getImage(image));
        }
    }

    public void setTooltip(String tooltip) {
        if (SystemTray.isSupported()) {
            trayIcon.setToolTip(tooltip);
        }
    }
}
