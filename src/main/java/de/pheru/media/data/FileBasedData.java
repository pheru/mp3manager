package de.pheru.media.data;

public abstract class FileBasedData {

    protected String fileName;
    protected String filePath;

    public FileBasedData() {
        //Default
    }

    public FileBasedData(final String fileName, final String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
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
