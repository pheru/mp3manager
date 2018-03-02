package de.pheru.media.core.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class ByteUtilTest {

    @Test
    public void bytesToMB() throws Exception {
        final long bytes = 5242880;
        final String bytesToMB = ByteUtil.bytesToMB(bytes);
        assertEquals("5,00 MB", bytesToMB);
    }

    @Test
    public void bytesToMB2() throws Exception {
        final long bytes = 5714739;
        final String bytesToMB = ByteUtil.bytesToMB(bytes);
        assertEquals("5,45 MB", bytesToMB);
    }

}