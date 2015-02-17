package de.eru.mp3manager.gui.utils;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;

/**
 *
 * @author Philipp Bruckner
 */
public class CssRowFactory<T> implements Callback<TableView<T>, TableRow<T>> {

    private final PseudoClass pseudoClass;
    private final ObservableList<Integer> styledIndices;
    private final Callback<TableView<T>, TableRow<T>> baseFactory;

    public CssRowFactory(String styleClass, Callback<TableView<T>, TableRow<T>> baseFactory) {
        this.pseudoClass = PseudoClass.getPseudoClass(styleClass);
        this.baseFactory = baseFactory;
        styledIndices = FXCollections.observableArrayList();
    }

    public CssRowFactory(String styleClass) {
        this(styleClass, null);
    }

    @Override
    public TableRow<T> call(TableView<T> tableView) {
        final TableRow<T> row;
        if (baseFactory == null) {
            row = new TableRow<>();
        } else {
            row = baseFactory.call(tableView);
        }

        styledIndices.addListener((ListChangeListener.Change<? extends Integer> c) -> {
            row.pseudoClassStateChanged(pseudoClass, styledIndices.contains(row.getIndex()));
        });

        row.indexProperty().addListener((observable, oldValue, newValue)
                -> row.pseudoClassStateChanged(pseudoClass, styledIndices.contains(row.getIndex()))
        );
        return row;
    }

    public ObservableList<Integer> getStyledIndices() {
        return styledIndices;
    }
}
