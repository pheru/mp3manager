package de.eru.mp3manager.data;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Eine injezierbare Liste von Mp3FileData-Objekten.
 *
 * @author Philipp Bruckner
 */
public class Mp3FileDataList {

    private ObservableList<Mp3FileData> data;
    private ListChangeListener listener;

    public ObservableList<Mp3FileData> getData() {
        return data;
    }

    public void setData(ObservableList<Mp3FileData> data) {
        this.data = data;
        if (listener != null) {
            data.addListener(listener);
        }
    }

    public void setListChangeListener(ListChangeListener listener) {
        this.listener = listener;
        if (data != null) {
            data.addListener(listener);
        }
    }
}