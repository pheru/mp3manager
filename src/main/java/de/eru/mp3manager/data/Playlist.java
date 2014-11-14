package de.eru.mp3manager.data;

import de.eru.mp3manager.Settings;
import de.eru.mp3manager.cdi.TableData;
import de.eru.mp3manager.cdi.TableDataSource;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * Klasse zum verwalten einer Wiedergabeliste.
 *
 * @author Philipp Bruckner
 */
@ApplicationScoped
public class Playlist extends FileBasedData {

    public static final String FILE_EXTENSION = "mmpl";
    public static final String FILE_SPLIT = System.lineSeparator();

    @Inject
    private Settings settings;

    private final BooleanProperty dirty = new SimpleBooleanProperty(false); // TODO wirklich nötig?
    private final ObservableList<Mp3FileData> titles = FXCollections.observableArrayList();
    private final ObservableList<Integer> randomIndicesToPlay = FXCollections.observableArrayList();
    private final IntegerProperty currentTitleIndex = new SimpleIntegerProperty(-1);

    @Inject
    private Event<Mp3FileData> currentTitleUpdateEvent;
    
    @PostConstruct
    private void init() {
        titles.addListener((ListChangeListener.Change<? extends Mp3FileData> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    if (change.getAddedSize() == titles.size()) {
                        currentTitleIndex.set(0);
                    }
                    for (int i = 0; i < change.getAddedSize(); i++) {
                        randomIndicesToPlay.add(Double.valueOf(Math.random() * (randomIndicesToPlay.size() - randomIndicesToPlay.indexOf(currentTitleIndex.get()))).intValue()
                                + randomIndicesToPlay.indexOf(currentTitleIndex.get()) + 1, randomIndicesToPlay.size());
                    }
                } else if (change.wasRemoved()) {
                    if (titles.isEmpty()) {
                        randomIndicesToPlay.clear();
                        currentTitleIndex.set(-1);
                    } else {
                        for (int i = change.getRemovedSize() - 1; i >= 0; i--) {
                            int currentRandomIndex = randomIndicesToPlay.indexOf(currentTitleIndex.get());
                            int removedIndex = change.getFrom() + i;
                            randomIndicesToPlay.remove(Integer.valueOf(removedIndex));
                            for (int j = 0; j < randomIndicesToPlay.size(); j++) {
                                if (randomIndicesToPlay.get(j) > removedIndex) {
                                    randomIndicesToPlay.set(j, randomIndicesToPlay.get(j) - 1);
                                }
                            }
                            if (settings.isMusicPlayerRandom()) {
                                if (currentRandomIndex == randomIndicesToPlay.size()) {
                                    resetRandomIndicesToPlay();
                                    currentTitleIndex.set(randomIndicesToPlay.get(0));
                                } else {
                                    currentTitleIndex.set(randomIndicesToPlay.get(currentRandomIndex));
                                }
                            } else {
                                if (removedIndex < currentTitleIndex.get()) {
                                    if (currentTitleIndex.get() > 0) {
                                        currentTitleIndex.set(currentTitleIndex.get() - 1);
                                    } else {
                                        currentTitleIndex.set(0);//titles.size() - 1);
                                    }
                                } else if (removedIndex == currentTitleIndex.get()) {
                                    currentTitleUpdateEvent.fire(titles.get(currentTitleIndex.get()));
                                }
                            }
                        }
                    }
                }
                //TODO Testausgaben entfernen
                System.out.println("-----------------");
                for (int i = 0; i < randomIndicesToPlay.size(); i++) {
                    System.out.print(randomIndicesToPlay.get(i));
                    System.out.println(" - " + titles.get(randomIndicesToPlay.get(i)).getTitle());
                }
            }
        }
        );
        settings.musicPlayerRandomProperty()
                .addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (newValue) {
                        Collections.swap(randomIndicesToPlay, 0, randomIndicesToPlay.indexOf(getCurrentTitleIndex()));
                    }
                }
                );
        filePath.bindBidirectional(settings.playlistFilePathProperty());
    }

    private void resetRandomIndicesToPlay() {
        Integer lastRandomIndex = randomIndicesToPlay.get(randomIndicesToPlay.size() - 1);
        Collections.shuffle(randomIndicesToPlay);
        if (randomIndicesToPlay.indexOf(lastRandomIndex) == 0) {
            Collections.swap(randomIndicesToPlay, 0, randomIndicesToPlay.size() - 1);
        }
    }

    /**
     *
     * @return true wenn ende der liste erreicht
     */
    public boolean next() {
        if (currentTitleIndex.get() == -1) {
            return true;
        }
        if (settings.isMusicPlayerRandom()) {
            boolean endOfListReached = false;
            int nextRandomIndex = randomIndicesToPlay.indexOf(currentTitleIndex.get()) + 1;
            if (nextRandomIndex == randomIndicesToPlay.size()) {
                resetRandomIndicesToPlay();
                nextRandomIndex = 0;
                endOfListReached = true;
            }
            currentTitleIndex.set(randomIndicesToPlay.get(nextRandomIndex));
            return endOfListReached;
        }
        if (currentTitleIndex.get() == titles.size() - 1) {
            currentTitleIndex.set(0);
            return true;
        } else {
            currentTitleIndex.set(currentTitleIndex.get() + 1);
            return false;
        }
    }

    public void previous() {
        if (currentTitleIndex.get() == -1) {
            return;
        }
        if (settings.isMusicPlayerRandom()) {
            int previousRandomIndex = randomIndicesToPlay.indexOf(currentTitleIndex.get()) - 1;
            if (previousRandomIndex < 0) {
                previousRandomIndex = randomIndicesToPlay.size() - 1;
            }
            currentTitleIndex.set(randomIndicesToPlay.get(previousRandomIndex));
        } else {
            if (currentTitleIndex.get() <= 0) {
                currentTitleIndex.set(titles.size() - 1);
            } else {
                currentTitleIndex.set(currentTitleIndex.get() - 1);
            }
        }
    }

    public ObservableList<Mp3FileData> getTitles() {
        return titles;
    }

    public Mp3FileData getCurrentTitle() {
        if (titles.isEmpty()) {
            return null;
        }
        if (currentTitleIndex.get() == -1) {
            currentTitleIndex.set(0);
        }
        return titles.get(currentTitleIndex.get());
    }

    public Integer getCurrentTitleIndex() {
        return currentTitleIndex.get();
    }

    public void setCurrentTitleIndex(final Integer currentTitleIndex) {
        this.currentTitleIndex.set(currentTitleIndex);
    }

    public IntegerProperty currentTitleIndexProperty() {
        return currentTitleIndex;
    }

    public Boolean isDirty() {
        return dirty.get();
    }

    public void setDirty(final Boolean dirty) {
        this.dirty.set(dirty);
    }

    public BooleanProperty dirtyProperty() {
        return dirty;
    }
}
