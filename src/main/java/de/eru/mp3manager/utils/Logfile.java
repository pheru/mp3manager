package de.eru.mp3manager.utils;

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
public final class Logfile {

    private Logfile() {
        //Utility-Klasse
    }

    /**
     * Schreibt ein Logfile mit Meldung und Stacktrace.
     *
     * @param t Das Throwable mit dem Stacktrace.
     * @param message Meldung des Fehlers.
     * @return Ob das Logfile erfolgreich geschrieben wurde.
     */
    public static boolean writeLogfile(Throwable t, String message) {
        FileWriter writer = null;
        try {
            DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            Date date = new Date();
            String time = format.format(date);
            StringBuilder content = new StringBuilder();
            content.append(time);
            content.append("  -  ");
            content.append(message);
            StackTraceElement[] stackTrace = t.getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                content.append("\n");
                content.append(stackTraceElement.toString());
            }
            File logfile = new File("E:\\logtest\\log.txt");
            logfile.createNewFile();
            writer = new FileWriter(logfile);
            writer.write(content.toString());
            writer.close();
            return true;
        } catch (IOException ex) {
            return false;
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                //TODO exHandling
            }
        }
    }
}
