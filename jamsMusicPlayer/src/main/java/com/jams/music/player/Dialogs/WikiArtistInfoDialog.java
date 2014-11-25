package com.jams.music.player.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jams.music.player.Parser.ParseCompleteListener;
import com.jams.music.player.Parser.WikiArtistInfoParser;
import com.jams.music.player.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 *
 */
public class WikiArtistInfoDialog extends DialogFragment {
    private String artist;

    private WikiArtistInfoParser parser;
    private TextView artistName;
    private TextView artistInfo;
    private SeekBar loadBar;

    private ImageView imageView;
    private String imgURL;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        artist = "";

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

                        try {
                            imgURL = parser.getImageURL();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        new GetArtistImageAsyncTask().execute(imgURL);
                    }
                }
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_wiki_artist_info, null);
        imageView =  (ImageView) view.findViewById(R.id.wiki_image_view);
        artistName = (TextView) view.findViewById(R.id.wiki_artist_name);
        artistInfo = (TextView) view.findViewById(R.id.wiki_artist_info);
        loadBar = (SeekBar) view.findViewById(R.id.seekBar);

        artistName.setText(artist);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.artist_info);
        builder.setView(view);
        return builder.create();
    }

    public void search(String artist, FragmentTransaction ft) {
        this.artist = artist;
        if(artistName != null)
            artistName.setText(artist);
        if(loadBar != null)
            loadBar.setVisibility(View.VISIBLE);
        parser.search(artist.replaceAll(" ", "_"));
        show(ft, "wikiArtistInfo");
    }

    private class GetArtistImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... url_array) {
            Log.i("Bla","In image async task");

            URL url;

            try {
                url = new URL(url_array[0]);
                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();

                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                input.close();
                return bitmap;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

}
