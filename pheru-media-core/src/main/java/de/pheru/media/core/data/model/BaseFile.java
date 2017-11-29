package de.pheru.media.core.data.model;

public abstract class BaseFile {

    protected String fileName;
    protected String filePath;

    public BaseFile() {
        //Default
    }

    public String getAbsolutePath() {
        return filePath + "\\" + fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
