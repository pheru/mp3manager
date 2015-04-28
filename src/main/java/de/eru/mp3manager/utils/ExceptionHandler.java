package de.eru.mp3manager.utils;

import de.eru.mp3manager.Mp3Manager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

/**
 * Klasse zum schreiben von Logfiles.
 *
 * @author Philipp Bruckner
 */
public final class ExceptionHandler {

    private ExceptionHandler() {
        //Utility-Klasse
    }

    public static void handle(Throwable t, String header, String text, boolean dialogWait, String logfileMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(!header.isEmpty() ? header : t.getClass().getSimpleName());
        alert.setContentText(!text.isEmpty() ? text : "Ein unerwarteter Fehler ist aufgetreten!");
        alert.getDialogPane().setPrefWidth(500.0);
        if (dialogWait) {
            alert.showAndWait();
        } else {
            alert.show();
        }
        writeLogfile(t, logfileMessage.isEmpty() ? "Unexpected Exception" : logfileMessage);
        t.printStackTrace();
    }

    public static void handle(Throwable t) {
        ExceptionHandler.handle(t, "", "", true, "");
    }

    public static void handle(Throwable t, String header, String text) {
        ExceptionHandler.handle(t, header, text, true, "");
    }

    public static void handle(Throwable t, String header, String text, String logfileMessage) {
        ExceptionHandler.handle(t, header, text, true, logfileMessage);
    }

    public static void handle(Throwable t, String header, String text, boolean dialogWait) {
        ExceptionHandler.handle(t, header, text, dialogWait, "");
    }

    public static void handle(Throwable t, String logfileMessage) {
        ExceptionHandler.handle(t, "", "", true, logfileMessage);
    }

    /**
     * Schreibt ein Logfile mit Meldung und Stacktrace.
     *
     * @param t Das Throwable mit dem Stacktrace.
     * @param message Meldung des Fehlers.
     */
    public static void writeLogfile(Throwable t, String message) { //TODO evtl. mit log4j ersetzen
        String stacktrace = getStacktraceAsString(t);
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date date = new Date();
        String time = format.format(date);
        StringBuilder content = new StringBuilder();
        content.append(time);
        if (!message.isEmpty()) {
            content.append("  -  ");
            content.append(message);
        }
        content.append(System.lineSeparator());
        content.append(stacktrace);

        File logfile = new File(Mp3Manager.APPLICATION_PATH + "/logs/" + new SimpleDateFormat("dd-MM-yyyy").format(date) + ".txt");
        int i = 2;
        while (logfile.exists()) {
            logfile = new File(Mp3Manager.APPLICATION_PATH + "/logs/" + new SimpleDateFormat("dd-MM-yyyy").format(date) + " (" + i + ").txt");
            i++;
        }
        if (logfile.getParentFile().exists() || logfile.getParentFile().mkdirs()) {
            try (FileWriter writer = new FileWriter(logfile)) {
                writer.write(content.toString());
                logfile.createNewFile();
            } catch (IOException ex) {
                createLogfileErrorDialog(t.getClass().getSimpleName(), stacktrace);
            }
        } else {
            createLogfileErrorDialog("Das Verzeichnis \"" + Mp3Manager.APPLICATION_PATH + "logs\" konnte nicht angelegt werden!", stacktrace);
        }
    }

    private static void createLogfileErrorDialog(String text, String stacktrace) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Fehler beim Erstellen des Logfiles!");
        alert.setContentText(text + "\nKopieren Sie sich den folgenden Inhalt bitte manuell:");
        alert.getDialogPane().setPrefWidth(11000.0);
        TextArea textArea = new TextArea(stacktrace);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        alert.getDialogPane().setExpandableContent(textArea);
        alert.getDialogPane().setExpanded(true);
        alert.showAndWait();
    }

    private static String getStacktraceAsString(Throwable t) {
        StringWriter stackTraceWriter = new StringWriter();
        t.printStackTrace(new PrintWriter(stackTraceWriter));
        return stackTraceWriter.toString();
    }
}
