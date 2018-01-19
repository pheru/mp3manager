package de.pheru.media.desktop.ui.application;

import de.pheru.fx.util.properties.ObservableProperties;
import de.pheru.media.core.data.model.AudioFile;
import de.pheru.media.core.util.ByteUtil;
import de.pheru.media.core.util.TimeUtil;
import de.pheru.media.desktop.cdi.qualifiers.Settings;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class  AudioFileTableView extends TableView<AudioFile> {

    @Inject
    @Settings
    private ObservableProperties settings;

    @PostConstruct
    public void init() {
        initColumns();
        applySettings();
    }

    private void initColumns() {
        addColumn("Dateiname", "fileName", String.class, null);
        addColumn("Titel", "title", String.class, null);
        addColumn("Album", "album", String.class, null);
        addColumn("Interpret", "artist", String.class, null);
        addColumn("Titelnummer", "track", Short.class, getShortCellFactory());
        addColumn("Dauer", "duration", Integer.class, getDurationCellFactory());
        addColumn("Genre", "genre", String.class, null);
        addColumn("Jahr", "year", Short.class, getShortCellFactory());
        addColumn("Dateigröße", "size", Long.class, getSizeCellFactory());
        addColumn("Bitrate", "bitrate", Short.class, getBitrateCellFactory());
    }

    private <T> void addColumn(final String columnName, final String propertyName, final Class<T> clazz,
                               final Callback<TableColumn<AudioFile, T>, TableCell<AudioFile, T>> cellFactory) {
        final TableColumn<AudioFile, T> tableColumn = new TableColumn<>(columnName);
        if (clazz.equals(Short.class) || clazz.equals(Integer.class) || clazz.equals(Long.class) ||
                clazz.equals(Float.class) || clazz.equals(Double.class)) {
            tableColumn.getStyleClass().add("column-align-right");
        }
        tableColumn.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        if (cellFactory != null) {
            tableColumn.setCellFactory(cellFactory);
        }
        getColumns().add(tableColumn);
    }

    private void applySettings() {
        //TODO
    }

    private Callback<TableColumn<AudioFile, Short>, TableCell<AudioFile, Short>> getShortCellFactory() {
        return new Callback<TableColumn<AudioFile, Short>, TableCell<AudioFile, Short>>() {
            @Override
            public TableCell<AudioFile, Short> call(TableColumn<AudioFile, Short> param) {
                return new TableCell<AudioFile, Short>() {
                    @Override
                    protected void updateItem(Short item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            if (item < 0) {
                                setText(null);
                            } else {
                                setText(String.valueOf(item));
                            }
                        }
                    }
                };
            }
        };
    }

    private Callback<TableColumn<AudioFile, Integer>, TableCell<AudioFile, Integer>> getDurationCellFactory() {
        return new Callback<TableColumn<AudioFile, Integer>, TableCell<AudioFile, Integer>>() {
            @Override
            public TableCell<AudioFile, Integer> call(TableColumn<AudioFile, Integer> param) {
                return new TableCell<AudioFile, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(TimeUtil.secondsToDurationFormat(item));
                        }
                    }
                };
            }
        };
    }

    private Callback<TableColumn<AudioFile, Short>, TableCell<AudioFile, Short>> getBitrateCellFactory() {
        return new Callback<TableColumn<AudioFile, Short>, TableCell<AudioFile, Short>>() {
            @Override
            public TableCell<AudioFile, Short> call(TableColumn<AudioFile, Short> param) {
                return new TableCell<AudioFile, Short>() {
                    @Override
                    protected void updateItem(Short item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            if (item < 0) {
                                setText(null);
                            } else {
                                setText(item + " kBit/s");
                            }
                        }
                    }
                };
            }
        };
    }

    private Callback<TableColumn<AudioFile, Long>, TableCell<AudioFile, Long>> getSizeCellFactory() {
        return new Callback<TableColumn<AudioFile, Long>, TableCell<AudioFile, Long>>() {
            @Override
            public TableCell<AudioFile, Long> call(TableColumn<AudioFile, Long> param) {
                return new TableCell<AudioFile, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            if (item < 0) {
                                setText(null);
                            } else {
                                setText(ByteUtil.bytesToMB(item));
                            }
                        }
                    }
                };
            }
        };
    }
}
