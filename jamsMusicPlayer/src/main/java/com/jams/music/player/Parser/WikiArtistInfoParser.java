package com.jams.music.player.Parser;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

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
    private static final String URL_WIKIMEDIA = "http://upload.wikimedia.org/wikipedia/commons/";

    private String imageURL;
    public ParseCompleteListener listener = null;

    WikiHandler handler;

    public void search(String titles) {
        if(titles != null && titles.length() > 0)
            new GetWikiInfoAsyncTask(titles).execute();
    }

    @Override
    public void onParseComplete(HashMap wikiInfo) {
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
            if (response != null) {
                new ParseWikiInfoAsyncTask(response).execute();
            }
        }
    }

    private class ParseWikiInfoAsyncTask extends AsyncTask<Void, Void, HashMap> {
        private InputStream response;

        public ParseWikiInfoAsyncTask(InputStream response) {
            this.response = response;
        }

        protected HashMap doInBackground(Void... nothing) {
            SAXParser parser;
            try {
                parser = SAXParserFactory.newInstance().newSAXParser();
            } catch (ParserConfigurationException e) {
                return null;
            } catch (SAXException e) {
                return null;
            }

            handler = new WikiHandler();
            try {
                parser.parse(response, handler);
            } catch (SAXException e) {
                return null;
            } catch (IOException e) {
                return null;
            }

            return handler.getWikiInfo();
        }

        @Override
        protected void onPostExecute(HashMap wikiInfo) {
            onParseComplete(wikiInfo);
        }
    }

    public String getImageURL(String fileName) {
        String digest = null;
        String subDirectory = null;

        // replace blank spaces with underscore
        fileName = fileName.replaceAll(" ", "_");

        // generate MD5 for this image filename
        digest = md5Java(fileName);    // generate MD5 for this image filename
        subDirectory = digest.charAt(0) +   // construct subdirectories to image
                    "/" +
                    digest.charAt(0) +
                    digest.charAt(1) +
                    "/";

        fileName = fileName.replaceAll("\\(", "%28");
        fileName = fileName.replaceAll("\\)", "%29");
        fileName = fileName.replaceAll(",", "%2C");

        imageURL = URL_WIKIMEDIA +
                subDirectory +
                fileName;

        return imageURL;
    }

    // Generate md5
    public static String md5Java(String message){
        String digest = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(message.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            digest = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (digest .length() < 32) {
                digest  = "0" + digest ;
            }
            return digest;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}