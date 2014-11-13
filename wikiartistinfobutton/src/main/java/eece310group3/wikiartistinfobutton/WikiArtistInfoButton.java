package eece310group3.wikiartistinfobutton;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 *
 */
public class WikiArtistInfoButton extends Button {
    private static final String TAG_WIKI = "wiki";

    private WikiArtistInfoDialog dialog;

    public WikiArtistInfoButton(Context context) {
        this(context, null, 0);
    }

    public WikiArtistInfoButton(Context context, AttributeSet attrs) {
       this(context, attrs, 0);
    }

    public WikiArtistInfoButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dialog = new WikiArtistInfoDialog();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: show dialog with appropiate FragmentManager
            }
        });
    }

    public void setArtistName(String artistName) {
        dialog.search(artistName);
    }
}
