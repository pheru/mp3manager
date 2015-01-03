package de.eru.mp3manager.utils;

import de.eru.mp3manager.Mp3Manager;
import de.eru.pherufxcontrols.dialogs.Dialog;
import de.eru.pherufxcontrols.dialogs.Dialogs;
import de.eru.pherufxcontrols.utils.InfoType;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Klasse zum schreiben von Logfiles.
 *
 * @author Philipp Bruckner
 */
public final class ExceptionHandler {

    private ExceptionHandler() {
        //Utility-Klasse
    }

    public static void handle(Throwable t, String dialogTitle, String dialogText, String logfileMessage) {
        Dialogs.createInfoDialog()
                .setType(InfoType.ERROR)
                .setTitle(dialogTitle.isEmpty() ? "Ein Fehler ist aufgetreten!" : dialogTitle)
                .setHeader(t.getLocalizedMessage())
                .setText(dialogText.isEmpty() ? "Es ist ein unerwartetes Problem aufgetreten!\nDetails befinden sich im Logfile." : dialogText)
                .showAndWait();
        writeLogfile(t, logfileMessage.isEmpty() ? "Unexpected Exception" : logfileMessage);
    }

    public static void handle(Throwable t) {
        ExceptionHandler.handle(t, "", "", "");
    }

    public static void handle(Throwable t, String dialogTitle, String dialogText) {
        ExceptionHandler.handle(t, dialogTitle, dialogText, "");
    }

    public static void handle(Throwable t, String logfileMessage) {
        ExceptionHandler.handle(t, "", "", logfileMessage);
    }

    /**
     * Schreibt ein Logfile mit Meldung und Stacktrace.
     *
     * @param t Das Throwable mit dem Stacktrace.
     * @param message Meldung des Fehlers.
     */
    public static void writeLogfile(Throwable t, String message) {
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date date = new Date();
        String time = format.format(date);
        StringBuilder content = new StringBuilder();
        content.append(time);
        if (!message.isEmpty()) {
            content.append("  -  ");
            content.append(message);
        }
        StackTraceElement[] stackTrace = t.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            content.append("\n");
            content.append(stackTraceElement.toString());
        }
        File logfile = new File(Mp3Manager.APPLICATION_PATH + "/logs/" + date.getTime() + ".txt");
        if (logfile.mkdirs()) {
            try (FileWriter writer = new FileWriter(logfile)) {
                writer.write(content.toString());
                logfile.createNewFile();
            } catch (IOException ex) {
                createLogfileErrorDialog(t.getLocalizedMessage());
            }
        } else {
            createLogfileErrorDialog("Das Verzeichnis \"" + Mp3Manager.APPLICATION_PATH + "/logs\" konnte nicht angelegt werden!");
        }
    }

    private static void createLogfileErrorDialog(String header) {
        Dialogs.createInfoDialog()
                .setType(InfoType.ERROR)
                .setTitle("Fehler beim Erstellen eines Logfiles!")
                .setHeader(header)
                .setText("Es konnte kein Logfile erzeugt werden! Kopieren Sie sich den folgenden Inhalt bitte manuell:")
                .showAndWait();
    }
}
