package com.jams.music.player.Parser;

import android.util.Log;

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
    private static final String REGEX_INFO_VAL = "\\s?=\\s?((?!\\{\\{).*?\\||(?=\\{\\{)(?:.*?\\}\\}\\|))";

    public static enum RegexInfo {
        image ("image"),
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
    private String filename;

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
        //HashMap<String,String> info = new HashMap<String,String>(artistInfo);
        return artistInfo;
    }

    private void parseWikiInfo(String content) {
        Pattern reg;
        Matcher regMatch;
        String regInfo;
        artistInfo = new HashMap<String, String>();

        for (RegexInfo regName : RegexInfo.values()) {
            reg = Pattern.compile(regName + REGEX_INFO_VAL);
            regMatch = reg.matcher(content);
            if (regMatch.find()) {
                Log.i("Wiki", "Group count:" + regMatch.groupCount() + " " + regMatch.group());
                regInfo = cleanUpInfo(regMatch.group(1), regName);
                if (regName == RegexInfo.image) {
                    // store image filename : "___.jpg"
                    filename = regName.toString();

                    artistInfo.put(regName.display(), regInfo);
                } else {
                    artistInfo.put(regName.display(), regInfo);
                }
            }
        }
    }

    public String getFileName() { return filename; }

    private String cleanUpInfo(String s, RegexInfo category) {
        Pattern reg;
        Matcher regMatch;
        String newInfo = s;
        switch (category) {
            case birth_date:
                String monthFirst = "yes";
                reg = Pattern.compile("(mf=)(\\w+)");
                regMatch = reg.matcher(s);
                if (regMatch.find()) {
                    monthFirst = regMatch.group(2).toLowerCase();
                }

                reg = Pattern.compile("(\\d{4})\\|(\\d{1,2})\\|(\\d{1,2})");
                regMatch = reg.matcher(s);
                if (regMatch.find()) {
                    if (monthFirst.contentEquals("yes")) {
                        newInfo = getMonth(regMatch.group(2)) + " " + regMatch.group(3) + ", " +
                                regMatch.group(1);
                    } else {
                        newInfo = getMonth(regMatch.group(3)) + " " + regMatch.group(2) + ", " +
                                regMatch.group(1);
                    }
                }
                break;
            default: // Remove references, side notes, and extra brackets/braces.
                // Regex to remove redundant info.
                reg = Pattern.compile("(\\|[\\w,' ',.,&]+\\])|((?:<.*>)|(?:[F,f]lat list)|(?:nowrap))");
                regMatch = reg.matcher(newInfo);
                while(regMatch.find()) {
                    if(regMatch.group(1) != null) {
                        Log.i("Wiki", "group1: " + regMatch.group());
                        newInfo = newInfo.replace(regMatch.group(1), ", ");
                    }
                    if(regMatch.group(2) != null) {
                        Log.i("Wiki", "group2: " + regMatch.group());
                        newInfo = newInfo.replace(regMatch.group(2), "");
                    }
                }
                Log.i("Wiki", "done: " + newInfo);
                newInfo = newInfo.replaceAll("[\\|\\{\\}\\*\\[\\]]","");
                break;
        }
        return newInfo;
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
