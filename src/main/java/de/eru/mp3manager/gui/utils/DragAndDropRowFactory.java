package de.eru.mp3manager.gui.utils;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.util.stream.Collectors;
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
public class DragAndDropRowFactory<T> implements Callback<TableView<T>, TableRow<T>> {

    private final Callback<TableView<T>, TableRow<T>> baseFactory;
    private VirtualFlow<?> virtualFlow;

    public DragAndDropRowFactory(TableView<T> table, Callback<TableView<T>, TableRow<T>> baseFactory) {
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
        
        row.setOnDragDetected(createDragDetectedHandler(row, tableView));
        row.setOnDragOver(createDragOverHandler(row));
        row.setOnDragExited(createDragExitedHandler(row));
        row.setOnDragDropped(createDragDroppedHandler(row, tableView));

        return row;
    }

    private EventHandler<MouseEvent> createDragDetectedHandler(TableRow<T> row, TableView<T> table) {
        return (MouseEvent event) -> {
            Dragboard db = row.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            String indicesAsString = table.getSelectionModel().getSelectedIndices().stream()
                    .map((Integer t) -> t.toString())
                    .collect(Collectors.joining("-"));
            content.putString(indicesAsString);
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

    private EventHandler<DragEvent> createDragDroppedHandler(TableRow<T> row, TableView<T> table) {
        return (DragEvent event) -> {
            Dragboard db = event.getDragboard();
            int targetIndex = row.getIndex();
            if (targetIndex < 0) {
                targetIndex = 0;
            } else if (targetIndex >= table.getItems().size()) {
                targetIndex = table.getItems().size() - 1; //TODO Kein -1
            }
            table.getSelectionModel().clearSelection();
            int movedCount = 0;
            for (String s : db.getString().split("-")) {
                int incomingIndex = Integer.parseInt(s);
                if (incomingIndex < targetIndex) {
                    T removed = table.getItems().remove(incomingIndex - movedCount);
                    table.getItems().add(targetIndex - 1, removed);
                } else if (incomingIndex > targetIndex) {
                    T removed = table.getItems().remove(incomingIndex);
                    table.getItems().add(targetIndex, removed);
                    targetIndex++;
                }
                movedCount++;
            }
            for (int i = 0; i < movedCount; i++) {
                table.getSelectionModel().select(targetIndex - 1 - i);
            }
            event.setDropCompleted(true);
        };
    }
}
