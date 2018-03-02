package de.pheru.media.desktop.cdi.producers;

import de.pheru.media.desktop.cdi.qualifiers.StartFinishedActions;
import de.pheru.media.desktop.util.PrioritizedRunnable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

@ApplicationScoped
public class PriorityQueueProducer {

    @Produces
    @StartFinishedActions
    @ApplicationScoped
    public SortedSet<PrioritizedRunnable> startFinishedActions() {
        return new TreeSet<>(Comparator.comparingInt(o -> o.getPriority().getValue()));
    }
}
