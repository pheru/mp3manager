package de.eru.mp3manager.gui.utils;

import java.util.ArrayList;
import java.util.List;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.util.Pair;

/**
 *
 * @author Philipp Bruckner
 */
public class DropCompletedEvent extends Event {

    public static final EventType<DropCompletedEvent> TYPE = new EventType<>("DnDCompleted");
    
    private final List<Pair<Integer, Integer>> movedIndices = new ArrayList<>();
    private int targetIndex;

    public DropCompletedEvent() {
        super(TYPE);
    }

    public List<Pair<Integer, Integer>> getMovedIndices() {
        return movedIndices;
    }

    public int getTargetIndex() {
        return targetIndex;
    }

    public void setTargetIndex(int targetIndex) {
        this.targetIndex = targetIndex;
    }

}
