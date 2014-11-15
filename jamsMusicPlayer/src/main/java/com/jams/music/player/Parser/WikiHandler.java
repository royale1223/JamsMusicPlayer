package com.jams.music.player.Parser;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class WikiHandler extends DefaultHandler {
    private static final String Q_NAME_REV = "rev";
    private static final String REGEX_INFO_VAL = "(\\s*)=(.*?)(((\\{\\{?)(.*?)(\\}\\}?)(\\s*))|\\|)";
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
    private String content = null;
    private HashMap<String, String> artistInfo;

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals(Q_NAME_REV)) {
            parseWikiInfo(content);
        }
    }

    @Override
    public void characters(char[] chars, int start, int length) throws SAXException {
        content += String.copyValueOf(chars, start, length).trim();
    }

    public HashMap getWikiInfo() {
        return artistInfo;
    }

    private void parseWikiInfo(String content) {
        Pattern reg;
        Matcher regMatch;
        artistInfo = new HashMap<String, String>();
        for (RegexInfo regName : RegexInfo.values()) {
            reg = Pattern.compile( regName + REGEX_INFO_VAL);
            regMatch = reg.matcher(content);
            if (regMatch.find()) {
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
