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
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(dialogTitle);
        alert.setContentText(dialogText);
        alert.show();
        writeLogfile(t, logfileMessage.isEmpty() ? "Unexpected Exception" : logfileMessage);
        t.printStackTrace();
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
        content.append(System.lineSeparator());
        content.append(getStacktraceAsString(t));
        
        File logfile = new File(Mp3Manager.APPLICATION_PATH + "/logs/" + new SimpleDateFormat("dd-MM-yyyy").format(date) + ".txt");
        int i = 2;
        while(logfile.exists()){
            logfile = new File(Mp3Manager.APPLICATION_PATH + "/logs/" + new SimpleDateFormat("dd-MM-yyyy").format(date) + " (" + i + ").txt");
            i++;
        }
        if (logfile.getParentFile().exists() || logfile.getParentFile().mkdirs()) {
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
//        Dialogs.createInfoDialog()
//                .setType(InfoType.ERROR)
//                .setTitle("Fehler beim Erstellen eines Logfiles!")
//                .setHeader(header)
//                .setText("Es konnte kein Logfile erzeugt werden! Kopieren Sie sich den folgenden Inhalt bitte manuell:")
//                .showAndWait();
    }
    
    private static String getStacktraceAsString(Throwable t){
        StringWriter stackTraceWriter = new StringWriter();
        t.printStackTrace(new PrintWriter(stackTraceWriter));
        return stackTraceWriter.toString();
    }
}
