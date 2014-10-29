package de.eru.mp3manager.gui.applicationwindow.playlist;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;

/**
 *
 * @author Philipp Bruckner
 */
public class PlaylistRowFactory<T> implements
        Callback<TableView<T>, TableRow<T>> {

    private final PseudoClass pseudoClass;
    private final IntegerProperty currentIndex;
    private final Callback<TableView<T>, TableRow<T>> baseFactory;

    /**
     * Construct a <code>PlaylistRowFactory</code>, specifying the name of the
     * pseudoclass that will be set for rows determined by
     * <code>getStyledRowIndices</code> and a base factory to create the
     * <code>TableRow</code>. If <code>baseFactory</code> is <code>null</code>,
     * default table rows will be created.
     *
     * @param styleClass The name of the pseudoclass that will be set for
     * specified rows.
     * @param baseFactory A factory for creating the rows. If null, default
     * <code>TableRow&lt;T&gt;</code>s will be created using the default
     * <code>TableRow</code> constructor.
     */
    public PlaylistRowFactory(String styleClass, Callback<TableView<T>, TableRow<T>> baseFactory) {
        this.pseudoClass = PseudoClass.getPseudoClass(styleClass);
        this.baseFactory = baseFactory;
        this.currentIndex = new SimpleIntegerProperty(-1);
    }

    /**
     * Construct a <code>PlaylistRowFactory</code>, which sets the pseudoclass
     * <code>styleClass</code> to true for rows determined by
     * <code>getStyledRowIndices</code>, and using default
     * <code>TableRow</code>s.
     *
     * @param styleClass
     */
    public PlaylistRowFactory(String styleClass) {
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

        currentIndex.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
                -> row.pseudoClassStateChanged(pseudoClass, currentIndex.get() == row.getIndex())
        );
        row.indexProperty().addListener((observable, oldValue, newValue)
                -> row.pseudoClassStateChanged(pseudoClass, currentIndex.get() == row.getIndex())
        );
        return row;
    }

    public Integer getCurrentIndex() {
        return currentIndex.get();
    }

    public void setCurrentIndex(final Integer currentIndex) {
        this.currentIndex.set(currentIndex);
    }

    public IntegerProperty currentIndexProperty() {
        return currentIndex;
    }

}
