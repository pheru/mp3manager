package de.pheru.media.gui.nodes;

import de.pheru.media.gui.applicationwindow.editfile.EditFilePresenter;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextFormatter;

/**
 *
 * @author Philipp Bruckner
 */
public class NumberComboBox<T> extends ComboBox<T> {

    public NumberComboBox() {
        addNumberFilter();
    }

    public NumberComboBox(ObservableList<T> items) {
        super(items);
        addNumberFilter();
    }

    private void addNumberFilter() {
        getEditor().setTextFormatter(new TextFormatter<>((TextFormatter.Change t) -> {
            if (!t.getText().matches("[0-9]*") && !t.getText().equals(EditFilePresenter.DIFF_VALUES)) {
                t.setText("");
            }
            return t;
        }));
    }
}
