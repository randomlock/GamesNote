package com.example.randomlocks.gamesnote.Modal.NewsModal;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

/**
 * Created by randomlocks on 6/30/2016.
 */
public class NewsModal extends RealmObject implements Parcelable {

    public String title;
    public String link;
    public String description;
    public String pubDate;
    public String content;
    public boolean isClicked;
    public String smallDescription;

    public NewsModal(String title, String link, String description, String pubDate, String content) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
        this.content = content;
    }


    public NewsModal() {
    }


    public List<NewsModal> parse(String in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            parser.setInput(new StringReader(in));

            parser.nextTag();
            return readFeed(parser);
        } finally {
            // in.close();
        }
    }

    private List<NewsModal> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<NewsModal> entries = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, null, "rss");


        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();


            if (name.equalsIgnoreCase("channel")) {

                while (true) {

                    int event = parser.next();

                    if (event == XmlPullParser.START_TAG && parser.getName().equals("item")) {
                        entries.add(readEntry(parser));

                    } else if (event == XmlPullParser.END_TAG && parser.getName().equals("channel")) {
                        break;
                    }

                }


            } else {
                skip(parser);
            }

        }


        return entries;
    }

    private NewsModal readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "item");
        String title = null;
        String link = null;
        String description = null;
        String pubDate = null;
        String content = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "title":
                    title = readTitle(parser);
                    break;
                case "link":
                    link = readLink(parser);
                    break;
                case "description":
                    description = readDescription(parser);
                    break;
                case "pubDate":
                    pubDate = readPubDate(parser);

                    break;
                case "media:content":
                    content = readMediaContent(parser);
                    break;

                case "enclosure":
                    content = readEnclosureContent(parser);
                    break;

                case "content:encoded":
                    description += readEncodedContent(parser);
                    break;


                default:
                    skip(parser);
                    break;
            }
        }
        return new NewsModal(title, link, description, pubDate, content);
    }


    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }


    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "title");
        return title;
    }

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "link");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "link");
        return link;
    }

    // Processes summary tags in the feed.
    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "description");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "description");
        return summary;
    }

    private String readEncodedContent(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "content:encoded");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "content:encoded");
        return summary;
    }


    private String readPubDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "pubDate");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "pubDate");
        return summary;
    }

    private String readMediaContent(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "media:content");
        String summary = parser.getAttributeValue(null, "url");
        parser.nextTag();
        return summary;
    }

    private String readEnclosureContent(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "enclosure");
        String summary = parser.getAttributeValue(null, "url");
        parser.nextTag();
        return summary;
    }



    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.link);
        dest.writeString(this.description);
        dest.writeString(this.pubDate);
        dest.writeString(this.content);
        dest.writeByte(this.isClicked ? (byte) 1 : (byte) 0);
        dest.writeString(this.smallDescription);
    }

    protected NewsModal(Parcel in) {
        this.title = in.readString();
        this.link = in.readString();
        this.description = in.readString();
        this.pubDate = in.readString();
        this.content = in.readString();
        this.isClicked = in.readByte() != 0;
        this.smallDescription = in.readString();
    }

    public static final Parcelable.Creator<NewsModal> CREATOR = new Parcelable.Creator<NewsModal>() {
        @Override
        public NewsModal createFromParcel(Parcel source) {
            return new NewsModal(source);
        }

        @Override
        public NewsModal[] newArray(int size) {
            return new NewsModal[size];
        }
    };
}
