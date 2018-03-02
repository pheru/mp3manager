package de.pheru.media.core.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TimeUtilTest {

    @Test
    public void secondsToDurationFormat0Seconds() throws Exception {
        final int seconds = 0;
        final String secondsToDuration = TimeUtil.secondsToDurationFormat(seconds);
        assertEquals("00:00", secondsToDuration);
    }

    @Test
    public void secondsToDurationFormat59Seconds() throws Exception {
        final int seconds = 59;
        final String secondsToDuration = TimeUtil.secondsToDurationFormat(seconds);
        assertEquals("00:59", secondsToDuration);
    }

    @Test
    public void secondsToDurationFormat60Seconds() throws Exception {
        final int seconds = 60;
        final String secondsToDuration = TimeUtil.secondsToDurationFormat(seconds);
        assertEquals("01:00", secondsToDuration);
    }

    @Test
    public void secondsToDurationFormat3599Seconds() throws Exception {
        final int seconds = 3599;
        final String secondsToDuration = TimeUtil.secondsToDurationFormat(seconds);
        assertEquals("59:59", secondsToDuration);
    }

    @Test
    public void secondsToDurationFormat3600Seconds() throws Exception {
        final int seconds = 3600;
        final String secondsToDuration = TimeUtil.secondsToDurationFormat(seconds);
        assertEquals("01:00:00", secondsToDuration);
    }

    @Test
    public void secondsToDurationFormat86399Seconds() throws Exception {
        final int seconds = 86399;
        final String secondsToDuration = TimeUtil.secondsToDurationFormat(seconds);
        assertEquals("23:59:59", secondsToDuration);
    }

    @Test
    public void secondsToDurationFormat86400Seconds() throws Exception {
        final int seconds = 86400;
        final String secondsToDuration = TimeUtil.secondsToDurationFormat(seconds);
        assertEquals("> 23:59:59", secondsToDuration);
    }
}