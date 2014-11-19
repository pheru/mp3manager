package de.eru.mp3manager.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Philipp Bruckner
 */
public abstract class FileBasedData {
    protected final StringProperty fileName = new SimpleStringProperty("");
    protected final StringProperty filePath = new SimpleStringProperty("");
    protected final StringProperty absolutePath = new SimpleStringProperty("");
    
    public FileBasedData(){
        absolutePath.bind(filePath.concat("\\").concat(fileName));
    }
    
    public String getFileName() {
        return fileName.get();
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public StringProperty fileNameProperty() {
        return fileName;
    }

    public String getFilePath() {
        return filePath.get();
    }

    public void setFilePath(String filePath) {
        this.filePath.set(filePath);
    }

    public StringProperty filePathProperty() {
        return filePath;
    }

    public String getAbsolutePath() {
        return absolutePath.get();
    }

    public StringProperty absolutePathProperty() {
        return absolutePath;
    }
}