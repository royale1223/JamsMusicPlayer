package eece310group3.wikiartistinfobutton.Parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 */
public class WikiHandler extends DefaultHandler {
    private static final String Q_NAME_REV = "rev";
    private static final String INFOBOX = "{{Infobox person";
    private static final String INFO_NAME = "birth_name";
    private static final String INFO_BIRTH_DATE = "birth_date";
    private static final String INFO_BIRTH_PLACE = "birth_place";
    private static final String INFO_YEARS_ACTIVE = "years_active";
    private static final String INFO_GENRE = "genre";

    private String content = null;
    private WikiInfo wikiInfo;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals(Q_NAME_REV))
            wikiInfo = new WikiInfo();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals(Q_NAME_REV))
            wikiInfo = parseWikiInfo(wikiInfo, content);
    }

    @Override
    public void characters(char[] chars, int start, int length) throws SAXException {
        content = String.copyValueOf(chars, start, length).trim();
    }

    public WikiInfo getWikiInfo() {
        return wikiInfo;
    }

    private WikiInfo parseWikiInfo(WikiInfo wikiInfo, String content) {
        int start = content.indexOf(INFOBOX);
        if (start == -1)
            return null;
        int end = infoboxEnd(start, content);

        String infoBox = content.substring(start, end);

        //TODO: parse info box to WikiInfo

        return wikiInfo;
    }

    private int infoboxEnd(int start, String content) {
        boolean ended = false;
        boolean bracket = false;
        int count = 1;
        start +=2;
        while(!ended) {
            if (content.charAt(start) == '{') {
                if (bracket) {
                    count++;
                }
                bracket = !bracket;
            } else if (content.charAt(start) == '}') {
                if (bracket) {
                    count--;
                }
                bracket = !bracket;
            }
            start++;

            if(count == 0)
                break;
        }

        return start;
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
