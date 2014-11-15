package com.jams.music.player.Parser;

import android.util.Log;
import android.util.Pair;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class WikiHandler extends DefaultHandler {
    private static final String Q_NAME_REV = "rev";
    private static final String REGEX_INFO_VAL = "(\\s*)=(.*?)\\|";
//    private static final String INFOBOX = "{{Infobox";
    public static enum RegexInfo {
        years_active ("Years Active: "),
        genre ("Genre: "),
        birth_date ("Birth Date: "),
        birth_place ("Birth Place: "),
        birth_name ("Birth Name: ");

        private final String displayText;
        RegexInfo(String display) {
            this.displayText = display;
        }

        String display() {
            return this.displayText;
        }

    }
 /*   private static final String INFO_NAME = "birth_name";
    private static final String INFO_BIRTH_DATE = "birth_date";
    private static final String INFO_BIRTH_PLACE = "birth_place";
    private static final String INFO_YEARS_ACTIVE = "years_active";
    private static final String INFO_GENRE = "genre";*/

    private String content = null;
    private WikiInfo wikiInfo;
    private HashMap<String, String> artistInfo;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals(Q_NAME_REV)) {
            wikiInfo = new WikiInfo();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals(Q_NAME_REV)) {
            parseWikiInfo(wikiInfo, content);
        }
    }

    @Override
    public void characters(char[] chars, int start, int length) throws SAXException {
        content += String.copyValueOf(chars, start, length).trim();
    }

    public HashMap getWikiInfo() {
        return artistInfo;
    }

    private void parseWikiInfo(WikiInfo wikiInfo, String content) {
        //TODO: parse info box to WikiInfo
        Pattern reg;
        Matcher regMatch;
        artistInfo = new HashMap<String, String>();
        for (RegexInfo regName : RegexInfo.values()) {
            reg = Pattern.compile( regName + REGEX_INFO_VAL);
            regMatch = reg.matcher(content);
            if (regMatch.find()) {
                Log.i("Wiki","regex:"+regMatch.group());
                artistInfo.put(regName.display(), regMatch.group());
            }
        }
    }

    private String getMonth(String monthString) {
        int month = Integer.parseInt(monthString);
        switch(month) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return "January";
        }
    }
}
