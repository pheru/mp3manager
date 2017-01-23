package de.pheru.media.gui.applicationwindow.main.table;

import de.pheru.fx.util.properties.ObservableProperties;
import de.pheru.media.data.Mp3FileData;
import de.pheru.media.util.ByteUtil;
import de.pheru.media.util.TimeUtil;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class MainTable extends TableView<Mp3FileData> {

    public MainTable() {
        initColumns();
    }

    public void applySettings(final ObservableProperties properties) {
        //TODO
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
        addColumn("Zuletzt bearbeitet", "lastModified", Long.class, getLastModifiedCellFactory());
    }

    private <T> void addColumn(final String columnName, final String propertyName, final Class<T> clazz,
            final Callback<TableColumn<Mp3FileData, T>, TableCell<Mp3FileData, T>> cellFactory) {
        final TableColumn<Mp3FileData, T> tableColumn = new TableColumn<>(columnName);
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

    private Callback<TableColumn<Mp3FileData, Short>, TableCell<Mp3FileData, Short>> getShortCellFactory() {
        return new Callback<TableColumn<Mp3FileData, Short>, TableCell<Mp3FileData, Short>>() {
            @Override
            public TableCell<Mp3FileData, Short> call(TableColumn<Mp3FileData, Short> param) {
                return new TableCell<Mp3FileData, Short>() {
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

    private Callback<TableColumn<Mp3FileData, Integer>, TableCell<Mp3FileData, Integer>> getDurationCellFactory() {
        return new Callback<TableColumn<Mp3FileData, Integer>, TableCell<Mp3FileData, Integer>>() {
            @Override
            public TableCell<Mp3FileData, Integer> call(TableColumn<Mp3FileData, Integer> param) {
                return new TableCell<Mp3FileData, Integer>() {
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

    private Callback<TableColumn<Mp3FileData, Short>, TableCell<Mp3FileData, Short>> getBitrateCellFactory() {
        return new Callback<TableColumn<Mp3FileData, Short>, TableCell<Mp3FileData, Short>>() {
            @Override
            public TableCell<Mp3FileData, Short> call(TableColumn<Mp3FileData, Short> param) {
                return new TableCell<Mp3FileData, Short>() {
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

    private Callback<TableColumn<Mp3FileData, Long>, TableCell<Mp3FileData, Long>> getLastModifiedCellFactory() {
        return new Callback<TableColumn<Mp3FileData, Long>, TableCell<Mp3FileData, Long>>() {
            @Override
            public TableCell<Mp3FileData, Long> call(TableColumn<Mp3FileData, Long> param) {
                return new TableCell<Mp3FileData, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(TimeUtil.millisecondsToDateFormat(item));
                        }
                    }
                };
            }
        };
    }

    private Callback<TableColumn<Mp3FileData, Long>, TableCell<Mp3FileData, Long>> getSizeCellFactory() {
        return new Callback<TableColumn<Mp3FileData, Long>, TableCell<Mp3FileData, Long>>() {
            @Override
            public TableCell<Mp3FileData, Long> call(TableColumn<Mp3FileData, Long> param) {
                return new TableCell<Mp3FileData, Long>() {
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
