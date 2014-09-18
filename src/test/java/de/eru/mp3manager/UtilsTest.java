package de.eru.mp3manager;

import de.eru.mp3manager.utils.TimeFormatter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Philipp Bruckner
 */
public class UtilsTest {

    public UtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void timeFormatterTest() {
        String formatted = "";
        double d = 444.0;
        formatted = TimeFormatter.secondsToDurationFormat(d, true);
        assertEquals("00:07:24", formatted);
        formatted = TimeFormatter.secondsToDurationFormat(d, false);
        assertEquals("07:24", formatted);
        d = 4444.0;
        formatted = TimeFormatter.secondsToDurationFormat(d, true);
        assertEquals("01:14:04", formatted);
        formatted = TimeFormatter.secondsToDurationFormat(d, false);
        assertEquals("01:14:04", formatted);
    }

//    @Test
//    public void logfileTest() {
//        String stringArray[] = new String[2];
//        try {
//            System.out.println(stringArray[7]);
//        } catch (Exception e) {
//            Logfile.writeLogfile(e, "Test-Fehler");
//        }
//    }
}
