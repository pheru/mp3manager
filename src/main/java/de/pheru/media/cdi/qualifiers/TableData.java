package de.pheru.media.cdi.qualifiers;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Qualifier für {@link de.pheru.media.cdi.producers.ObservableListProducer}.
 */
@Qualifier
@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER, TYPE})
public @interface TableData {

    Source value();

    enum Source {

        MAIN, MAIN_SELECTED
    }
}
