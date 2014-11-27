package com.jams.music.player.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jams.music.player.Helpers.TypefaceHelper;
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
    private WikiArtistInfoParser parser;
    private String artist;
    private String imgURL;
    private String img;
    private View view;
    private TextView artistName;
    private TextView artistInfo;
    private TextView pleaseWait;
    private ImageView imageView;
    private LinearLayout loadingLayout;
    private ProgressBar progressBar;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parser = new WikiArtistInfoParser();
        parser.setParseCompleteListener(new ParseCompleteListener() {
            @Override
            public void onParseComplete(HashMap wikiInfo) {

                if (wikiInfo == null || wikiInfo.isEmpty()) {
                    artistName.setText(artist);
                    artistInfo.setText("Sorry, could not find artist");
                    artistName.setVisibility(View.VISIBLE);
                    artistInfo.setVisibility(View.VISIBLE);
                    loadingLayout.setVisibility(View.GONE);
                } else {
                    if (artistInfo != null) {
                        String info = "";

                        for (Object s : wikiInfo.keySet()) {
                            if (s.toString().equals("image")) {
                                img = (String) wikiInfo.get(s);
                            } else {

                                info += s.toString();
                                info += wikiInfo.get(s);
                                info += "\n";
                            }
                        }

                        artistName.setText(artist);
                        artistInfo.setText(info);

                        loadingLayout.setVisibility(View.GONE);

                        artistName.setVisibility(View.VISIBLE);
                        artistInfo.setVisibility(View.VISIBLE);

                        try {
                            imgURL = parser.getImageURL(img);
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
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_wiki_artist_info, null);
        imageView =  (ImageView) view.findViewById(R.id.wiki_image_view);

        artistName = (TextView) view.findViewById(R.id.wiki_artist_name);
        artistName.setTypeface(TypefaceHelper.getTypeface(getActivity(), "RobotoCondensed-Bold"));

        artistInfo = (TextView) view.findViewById(R.id.wiki_artist_info);
        artistInfo.setTypeface(TypefaceHelper.getTypeface(getActivity(), "RobotoCondensed-Light"));

        // while loading content view
        loadingLayout = (LinearLayout) view.findViewById(R.id.wiki_wait_layout);
        pleaseWait = (TextView) view.findViewById(R.id.wiki_please_wait);
        pleaseWait.setTypeface(TypefaceHelper.getTypeface(getActivity(), "RobotoCondensed-Light"));
        progressBar = (ProgressBar) view.findViewById(R.id.wiki_progressBar);

        imageView.setVisibility(View.GONE);
        artistName.setVisibility(View.GONE);
        artistInfo.setVisibility(View.GONE);

        loadingLayout.setVisibility(View.VISIBLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      //  builder.setTitle("About...");
        builder.setView(view);

        return builder.create();
    }

    public void search(String artist, FragmentTransaction ft) {
        this.artist = artist;
        if(artistName != null)
            artistName.setText(artist);
        parser.search(artist.replaceAll(" ", "_"));
        show(ft, "wikiArtistInfo");
    }

    private class GetArtistImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... url_array) {
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
            if (result != null) {
                imageView.setImageBitmap(result);
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.GONE);
            }
        }
    }

}
