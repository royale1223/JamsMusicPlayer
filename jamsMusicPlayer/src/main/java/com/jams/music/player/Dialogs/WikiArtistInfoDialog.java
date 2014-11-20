package com.jams.music.player.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jams.music.player.Parser.ParseCompleteListener;
import com.jams.music.player.Parser.WikiArtistInfoParser;
import com.jams.music.player.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 *
 */
public class WikiArtistInfoDialog extends DialogFragment {
    private WikiArtistInfoParser parser;
    private TextView artistName;
    private TextView artistInfo;
    private SeekBar loadBar;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parser = new WikiArtistInfoParser();
        parser.setParseCompleteListener(new ParseCompleteListener() {
            @Override
            public void onParseComplete(HashMap wikiInfo) {
                if(wikiInfo == null || wikiInfo.isEmpty()) {
                    artistInfo.setText("Could not find artist");
                } else {
                    if (artistInfo != null) {
                        String info = "";
                        for (Object s : wikiInfo.keySet()) {
                            //TODO: Clean up wikiInfo values
                            info += s.toString();
                            info += wikiInfo.get(s);
                            info += "\n";
                        }
                       artistInfo.setText(info);
                       loadBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_wiki_artist_info, null);
        artistName = (TextView) view.findViewById(R.id.wiki_artist_name);
        artistInfo = (TextView) view.findViewById(R.id.wiki_artist_info);
        loadBar = (SeekBar) view.findViewById(R.id.seekBar);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.artist_info);
        builder.setView(view);
        return builder.create();
    }

    public void search(String titles, FragmentTransaction ft) {
        artistName.setText(titles);
        String artist = tiles.replace(" ", "_");
        loadBar.setVisibility(View.VISIBLE);
        parser.search(titles);
        show(ft, "wikiArtistInfo");
    }
}
