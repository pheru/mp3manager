/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.eru.mp3manager;

import de.eru.mp3manager.utils.Command;
import java.awt.AWTException;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author Philipp Bruckner
 */
public class Mp3SystemTrayIcon {

    public static final Mp3SystemTrayIcon INSTANCE = new Mp3SystemTrayIcon();

    private TrayIcon trayIcon;

    private Mp3SystemTrayIcon() {
        if (SystemTray.isSupported()) {
            SystemTray systemTray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("img/trayIcon.jpg").getPath());
            trayIcon = new TrayIcon(image, "Mp3Manager", new PopupMenu());
            trayIcon.setImageAutoSize(true);
            trayIcon.getPopupMenu().setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            try {
                systemTray.add(trayIcon);
            } catch (AWTException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void displayMessage(String caption, String message, MessageType messageType) {
        trayIcon.displayMessage(caption, message, messageType);
    }

    public void addPopUpMenuItem(String label, ActionListener actionListener) {
        if (SystemTray.isSupported()) {
            MenuItem menuItem = new MenuItem(label);
            menuItem.addActionListener(actionListener);
            trayIcon.getPopupMenu().add(menuItem);
        }
    }

    public void addOnClick(Command command) {
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
                        command.execute();
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
