package com.jams.music.player.test;

import android.test.InstrumentationTestCase;
import com.jams.music.player.Parser.*;

public class ParserTest extends InstrumentationTestCase {

    private WikiArtistInfoParser parser;

    // dummy test
    public void testParse(){
        int expected = 5;
        int actual = 5;
        assertEquals(expected, actual);
    }

}
