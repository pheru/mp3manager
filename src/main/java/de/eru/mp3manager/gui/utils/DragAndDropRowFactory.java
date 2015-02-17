package de.eru.mp3manager.gui.utils;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;

/**
 *
 * @author Philipp Bruckner
 */
public class DragAndDropRowFactory<T, S> implements Callback<TableView<T>, TableRow<T>> {

    private final Callback<TableView<T>, TableRow<T>> baseFactory;
    private TableView<T> table;
    private VirtualFlow<?> virtualFlow;

    public DragAndDropRowFactory(TableView<T> table, Callback<TableView<T>, TableRow<T>> baseFactory) {
        this.table = table;
        this.baseFactory = baseFactory;
        Platform.runLater(() -> {
            TableViewSkin<?> tableSkin = (TableViewSkin<?>) table.getSkin();
            virtualFlow = (VirtualFlow<?>) tableSkin.getChildren().get(1);
        });
    }

    public DragAndDropRowFactory(TableView<T> table) {
        this(table, null);
    }

    @Override
    public TableRow<T> call(TableView<T> tableView) {
        final TableRow<T> row;
        if (baseFactory == null) {
            row = new TableRow<>();
        } else {
            row = baseFactory.call(tableView);
        }
        
        row.setOnDragDetected(createDragDetectedHandler(row));
        row.setOnDragOver(createDragOverHandler(row));
        row.setOnDragExited(createDragExitedHandler(row));
        row.setOnDragDropped(createDragDroppedHandler(row));

        return row;
    }

    private EventHandler<MouseEvent> createDragDetectedHandler(TableRow<T> row) {
        return (MouseEvent event) -> {
            Dragboard db = row.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(row.getIndex()));
            db.setContent(content);
        };
    }

    private EventHandler<DragEvent> createDragOverHandler(TableRow<T> row) {
        return (DragEvent event) -> {
            if (event.getDragboard().hasString()) {
                if (!row.getStyleClass().contains("drag-over")) {
                    row.getStyleClass().add("drag-over");
                }
                if (row.getIndex() <= virtualFlow.getFirstVisibleCell().getIndex() + 1) {
                    virtualFlow.adjustPixels(-5);
                }
                if (row.getIndex() >= virtualFlow.getLastVisibleCell().getIndex() - 1) {
                    virtualFlow.adjustPixels(5);
                }
                event.acceptTransferModes(TransferMode.ANY);
            }
        };
    }

    private EventHandler<DragEvent> createDragExitedHandler(TableRow<T> row) {
        return (DragEvent event) -> {
            row.getStyleClass().remove("drag-over");
        };
    }

    private EventHandler<DragEvent> createDragDroppedHandler(TableRow<T> row) {
        return (DragEvent event) -> {
            Dragboard db = event.getDragboard();
            int myIndex = row.getIndex();
            if (myIndex < 0 || myIndex >= table.getItems().size()) {
                myIndex = table.getItems().size() - 1;
            }
            int incomingIndex = Integer.parseInt(db.getString());
            System.out.println(incomingIndex + " ---> " + myIndex);
            T removed = table.getItems().remove(incomingIndex);
            table.getItems().add(myIndex, removed);
            table.getSelectionModel().clearAndSelect(myIndex);
            event.setDropCompleted(true);
        };
    }

}
