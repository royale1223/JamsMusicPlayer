package eece310group3.wikiartistinfobutton.Parser;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 *
 */
public class WikiArtistInfoParser implements ParseCompleteListener {
    //https://en.wikipedia.org/w/api.php?action=query&continue=&format=xmlfm&prop=revisions&redirects=true&rvprop=content&rvsection=0&titles=
    private static final String URL_WIKI_API = "https://en.wikipedia.org/w/api.php?";
    private static final String PROPERTY_ACTION = "action=query&";
    private static final String PROPERTY_CONTINUE = "continue=&";
    private static final String PROPERTY_FORMAT = "format=xml&";
    private static final String PROPERTY_PROP = "prop=revisions&";
    private static final String PROPERTY_REDIRECTS = "redirects=true&";
    private static final String PROPERTY_RVPROP = "rvprop=content&";
    private static final String PROPERTY_RVSECTION = "rvsection=0&";
    private static final String PROPERTY_TITLES = "titles=";


    public ParseCompleteListener listener = null;

    public void search(String titles) {
        if(titles != null && titles.length() > 0)
            new GetWikiInfoAsyncTask(titles).execute();

    }

    @Override
    public void onParseComplete(WikiInfo wikiInfo) {
        if(listener != null)
            listener.onParseComplete(wikiInfo);
    }

    public void setParseCompleteListener(ParseCompleteListener listener) {
        this.listener = listener;
    }

    private class GetWikiInfoAsyncTask extends AsyncTask<Void, Void, InputStream> {
        private String titles;

        public GetWikiInfoAsyncTask(String titles) {
            this.titles = titles;
        }

        protected InputStream doInBackground(Void... nothing) {
            String url = URL_WIKI_API +
                    PROPERTY_ACTION +
                    PROPERTY_CONTINUE +
                    PROPERTY_FORMAT +
                    PROPERTY_PROP +
                    PROPERTY_REDIRECTS +
                    PROPERTY_RVPROP +
                    PROPERTY_RVSECTION +
                    PROPERTY_TITLES +
                    titles;

            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpResponse httpResponse = httpClient.execute(new HttpGet(url));
                return httpResponse.getEntity().getContent();
            } catch (IOException e) {
                return null;
            }
        }

        protected void onPostExecute(InputStream response) {
            if (response != null)
                new ParseWikiInfoAsyncTask(response).execute();

        }
    }

    private class ParseWikiInfoAsyncTask extends AsyncTask<Void, Void, WikiInfo> {
        private InputStream response;

        public ParseWikiInfoAsyncTask(InputStream response) {
            this.response = response;
        }

        protected WikiInfo doInBackground(Void... nothing) {
            SAXParser parser;
            try {
                parser = SAXParserFactory.newInstance().newSAXParser();
            } catch (ParserConfigurationException e) {
                return null;
            } catch (SAXException e) {
                return null;
            }

            WikiHandler handler = new WikiHandler();
            try {
                parser.parse(response, handler);
            } catch (SAXException e) {
                return null;
            } catch (IOException e) {
                return null;
            }
            return handler.getWikiInfo();

        }

        protected void onPostExecute(WikiInfo wikiInfo) {
            onParseComplete(wikiInfo);
        }
    }
}
