package de.eru.mp3manager.data;

import de.eru.mp3manager.settings.Settings;
import de.eru.mp3manager.cdi.CurrentTitleEvent;
import de.eru.mp3manager.cdi.Updated;
import de.eru.mp3manager.cdi.XMLSettings;
import de.eru.mp3manager.service.FileService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
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

    public static final int UNDEFINED_CURRENT_INDEX = -42;
    public static final String FILE_EXTENSION = "mmpl";
    public static final String FILE_SPLIT = System.lineSeparator();

    @Inject
    @XMLSettings
    private Settings settings;
    @Inject
    @Updated
    private Event<CurrentTitleEvent> currentTitleUpdateEvent;

    private final BooleanProperty dirty = new SimpleBooleanProperty(false);
    private final ObservableList<Mp3FileData> titles = FXCollections.observableArrayList();
    private final ObservableList<Integer> randomIndicesToPlay = FXCollections.observableArrayList();
    private final IntegerProperty currentTitleIndex = new SimpleIntegerProperty(UNDEFINED_CURRENT_INDEX);

    @PostConstruct
    private void init() {
        currentTitleIndex.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            int newValueInt = newValue.intValue();
            if (newValueInt != UNDEFINED_CURRENT_INDEX) {
                currentTitleUpdateEvent.fire(new CurrentTitleEvent(titles.get(newValueInt), newValueInt));
            } else {
                currentTitleUpdateEvent.fire(new CurrentTitleEvent(Mp3FileData.MUSICPLAYER_PLACEHOLDER_DATA, newValueInt));
            }
        });
        settings.musicPlayerRandomProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                initRandomIndicesToPlay();
            }
        });
    }

    /**
     * @deprecated Zugriff sollte direkt auf die Liste geschehen (Listener um
     * Indizes zu aktualisieren nötig)
     */
    public void add(List<Mp3FileData> dataToAdd) {
        titles.addAll(dataToAdd);
        if (dataToAdd.size() == titles.size()) {
            setCurrentTitleIndex(0);
        }
        for (int i = 0; i < dataToAdd.size(); i++) {
            randomIndicesToPlay.add(Double.valueOf(Math.random() * (randomIndicesToPlay.size()
                    - randomIndicesToPlay.indexOf(currentTitleIndex.get()))).intValue()
                    + randomIndicesToPlay.indexOf(currentTitleIndex.get()) + 1,
                    randomIndicesToPlay.size());
        }
        setDirtyByCheck();
    }

    //TODO kann etwas refactored werden (event wird mit jedem schleifendurchlauf gefeuert)
    /**
     * @deprecated Zugriff sollte direkt auf die Liste geschehen (Listener um Indizes zu aktualisieren nötig)
     */
    public void remove(List<Integer> selectedIndices) {
        List<Integer> indicesToRemove = new ArrayList<>(selectedIndices);
        if (titles.size() == indicesToRemove.size()) {
            titles.clear();
            randomIndicesToPlay.clear();
            setCurrentTitleIndex(UNDEFINED_CURRENT_INDEX);
        } else {
            for (int i = indicesToRemove.size() - 1; i >= 0; i--) {
                //Titel entfernen
                titles.remove(indicesToRemove.get(i).intValue());
                
                // Aktuellen zufälligen Index anpassen
                int currentRandomIndex = randomIndicesToPlay.indexOf(currentTitleIndex.get());
                if (randomIndicesToPlay.indexOf(indicesToRemove.get(i)) < currentRandomIndex) {
                    currentRandomIndex--;
                }
                
                //Index entfernen und folgende Indizes anpassen
                randomIndicesToPlay.remove(indicesToRemove.get(i));
                for (int j = 0; j < randomIndicesToPlay.size(); j++) {
                    if (randomIndicesToPlay.get(j) > indicesToRemove.get(i)) {
                        randomIndicesToPlay.set(j, randomIndicesToPlay.get(j) - 1);
                    }
                }
                
                //Aktuellen Index anpassen
                if (settings.isMusicPlayerRandom()) {
                    if (currentRandomIndex >= randomIndicesToPlay.size()) {
                        resetRandomIndicesToPlay();
                        setCurrentTitleIndex(randomIndicesToPlay.get(0));
                    } else {
                        setCurrentTitleIndex(randomIndicesToPlay.get(currentRandomIndex));
                    }
                } else {
                    if (indicesToRemove.get(i) < currentTitleIndex.get()) {
                        setCurrentTitleIndex(currentTitleIndex.get() - 1);
                    } else if (indicesToRemove.get(i) == currentTitleIndex.get()) {// && currentTitleIndex.get() >= titles.size()) {
                        if (currentTitleIndex.get() < titles.size()) {
                            setCurrentTitleIndex(getCurrentTitleIndex());
                        } else {
                            setCurrentTitleIndex(titles.size() - 1);
                        }
                    }
                }
            }
        }
        setDirtyByCheck();
//        System.out.println("-----------------");
//        for (int j = 0; j < randomIndicesToPlay.size(); j++) {
//            System.out.print(randomIndicesToPlay.get(j));
//            System.out.println(" - " + titles.get(randomIndicesToPlay.get(j)).getTitle());
//        }
    }
    
    /**
     * @deprecated Zugriff sollte direkt auf die Liste geschehen (Listener um Indizes zu aktualisieren nötig)
     */
    public void clear(){ 
        List<Integer> indicesToRemove = new ArrayList();
        for(int i = 0; i < titles.size(); i++){
            indicesToRemove.add(i);
        }
        remove(indicesToRemove);
    }
    
    /**
     *
     * @return true, wenn Playlist "dirty"
     */
    private boolean checkIfDirty() {
        if (fileName.get().isEmpty()) {
            return false;
        }
        try {
            List<String> filePaths = FileService.loadPlaylist(new File(absolutePath.get()));
            if (titles.size() != filePaths.size()) {
                return true;
            }
            for (int i = 0; i < filePaths.size(); i++) {
                if (!titles.get(i).getAbsolutePath().equals(filePaths.get(i))) {
                    return true;
                }
            }
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            //TODO Meldung + Möglichkeit Playlist wiederherzustellen
            alert.showAndWait();
        }
        return false;
    }

    public void setDirtyByCheck() {
        dirty.set(checkIfDirty());
    }

    private void initRandomIndicesToPlay() {
        if (!randomIndicesToPlay.isEmpty()) {
            Collections.shuffle(randomIndicesToPlay);
            Collections.swap(randomIndicesToPlay, 0, randomIndicesToPlay.indexOf(currentTitleIndex.get()));
        }
    }

    private void resetRandomIndicesToPlay() {
        if (!randomIndicesToPlay.isEmpty()) {
            Integer lastRandomIndex = randomIndicesToPlay.get(randomIndicesToPlay.size() - 1);
            Collections.shuffle(randomIndicesToPlay);
            if (randomIndicesToPlay.indexOf(lastRandomIndex) == 0) {
                Collections.swap(randomIndicesToPlay, 0, randomIndicesToPlay.size() - 1);
            }
        }
    }

    /**
     *
     * @return true wenn ende der liste erreicht
     */
    public boolean next() {
        boolean reachedEndOfList = false;
        if (currentTitleIndex.get() == UNDEFINED_CURRENT_INDEX) {
            return false;
        } else if (settings.isMusicPlayerRandom()) {
            int nextRandomIndex = randomIndicesToPlay.indexOf(currentTitleIndex.get()) + 1;
            if (nextRandomIndex == randomIndicesToPlay.size()) {
                resetRandomIndicesToPlay();
                nextRandomIndex = 0;
                reachedEndOfList = true;
            }
            setCurrentTitleIndex(randomIndicesToPlay.get(nextRandomIndex));
        } else if (currentTitleIndex.get() == titles.size() - 1) {
            setCurrentTitleIndex(0);
            reachedEndOfList = true;
        } else {
            setCurrentTitleIndex(currentTitleIndex.get() + 1);
            reachedEndOfList = false;
        }
        return reachedEndOfList;
    }

    public void previous() {
        if (currentTitleIndex.get() == UNDEFINED_CURRENT_INDEX) {
            return;
        }
        if (settings.isMusicPlayerRandom()) {
            int previousRandomIndex = randomIndicesToPlay.indexOf(currentTitleIndex.get()) - 1;
            if (previousRandomIndex < 0) {
                previousRandomIndex = randomIndicesToPlay.size() - 1;
            }
            setCurrentTitleIndex(randomIndicesToPlay.get(previousRandomIndex));
        } else {
            if (currentTitleIndex.get() <= 0) {
                setCurrentTitleIndex(titles.size() - 1);
            } else {
                setCurrentTitleIndex(currentTitleIndex.get() - 1);
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
        if (currentTitleIndex.get() == UNDEFINED_CURRENT_INDEX) {
            setCurrentTitleIndex(0);
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
