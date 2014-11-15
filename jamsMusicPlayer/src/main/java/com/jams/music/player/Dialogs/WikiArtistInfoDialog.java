package com.jams.music.player.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jams.music.player.Parser.ParseCompleteListener;
import com.jams.music.player.Parser.WikiArtistInfoParser;
import com.jams.music.player.Parser.WikiInfo;
import com.jams.music.player.Parser.WikiHandler.RegexInfo;
import com.jams.music.player.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 *
 */
public class WikiArtistInfoDialog extends DialogFragment {
    private boolean parseComplete;
    private WikiArtistInfoParser parser;
    private WikiInfo w;
    private TextView artistInfo;


    static public WikiArtistInfoDialog newInstance(String artist) {
        WikiArtistInfoDialog f = new WikiArtistInfoDialog();
        Bundle args = new Bundle();
        args.putString("artist", artist);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseComplete = false;
        String artist = getArguments().getString("artist");
        try {
            artist = URLEncoder.encode(artist, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        parser = new WikiArtistInfoParser();
        parser.search(artist);
        parser.setParseCompleteListener(new ParseCompleteListener() {
            @Override
            public void onParseComplete(HashMap wikiInfo) {
                if(wikiInfo == null) {
                    //TODO: show a view that states the artist cannot be found and perhaps let the user enter another name
                    artistInfo.setText("Could not find artist");
                } else {
                    //TODO: set all the TextViews with the appropiate artist info
                    if (artistInfo != null) {
                        String a = "";
                        for (Object s : wikiInfo.values()) {
                            a += s.toString();
                            a += wikiInfo.get(s);
                            a += "\n";
                        }
                       artistInfo.setText(a);
                    }
                }
                parseComplete = true;
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //TODO: create a view with a layout that will display the artist info
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_wiki_artist_info, null);
        String artist = getArguments().getString("artist");
        TextView artistName = (TextView) view.findViewById(R.id.wiki_artist_name);
        artistInfo = (TextView) view.findViewById(R.id.wiki_artist_info);
        artistName.setText(artist);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.artist_info);
        builder.setView(view);
        return builder.create();
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
