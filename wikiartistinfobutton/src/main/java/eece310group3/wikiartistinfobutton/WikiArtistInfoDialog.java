package eece310group3.wikiartistinfobutton;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import eece310group3.wikiartistinfobutton.Parser.ParseCompleteListener;
import eece310group3.wikiartistinfobutton.Parser.WikiArtistInfoParser;
import eece310group3.wikiartistinfobutton.Parser.WikiInfo;

/**
 *
 */
public class WikiArtistInfoDialog extends DialogFragment {
    private boolean parseComplete;
    private WikiArtistInfoParser parser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        parseComplete = false;
        parser = new WikiArtistInfoParser();
        parser.setParseCompleteListener(new ParseCompleteListener() {
            @Override
            public void onParseComplete(WikiInfo wikiInfo) {
                if(wikiInfo == null) {
                    //TODO: show a view that states the artist cannot be found and perhaps let the user enter another name
                } else {
                    //TODO: set all the TextViews with the appropiate artist info
                }
                parseComplete = true;
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //TODO: create a view with a layout that will display the artist info
        return null;
    }

    public void search(String titles) {
        parser.search(titles);
        parseComplete = false;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (parseComplete) {
            super.show(manager, tag);
        } else {
            //TODO: show a view that indicates parsing hasn't completed yet
        }
    }
}
