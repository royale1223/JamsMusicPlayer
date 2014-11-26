package com.jams.music.player.test;

import android.test.InstrumentationTestCase;
import com.jams.music.player.Parser.*;

import java.util.HashMap;

public class ParserTest extends InstrumentationTestCase {

    private WikiArtistInfoParser parser;

    // dummy test
    public void testParse(){
        int expected = 5;
        int actual = 5;
        assertEquals(expected, actual);
    }

    public void test_artist_search(){
        WikiHandler handler = new WikiHandler();
        HashMap<String, String> artistInfo = handler.getWikiInfo();

        parser.search("Coldplay");
        parser.onParseComplete(artistInfo);


    }
}
