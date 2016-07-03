package com.example.randomlocks.gamesnote.Modal.NewsModal;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Xml;

import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by randomlocks on 6/30/2016.
 */
public class NewsModal   {

    public   String title;
    public   String link;
    public   String description;
    public String pubDate;
    public String content;
    public boolean isClicked;

    public NewsModal(String title, String link, String description, String pubDate,String content) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
        this.content = content;
        isClicked = false;
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

        parser.require(XmlPullParser.START_TAG, null,"rss");




        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();


            if(name.equalsIgnoreCase("channel")){

                while (true) {

                    int event = parser.next();

                    if(event==XmlPullParser.START_TAG && parser.getName().equals("item")){
                        entries.add(readEntry(parser));

                    }

                    else if(event==XmlPullParser.END_TAG && parser.getName().equals("channel")){
                        break;
                    }

                }




            }else {
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
                default:
                    skip(parser);
                    break;
            }
        }
        return new NewsModal(title,link,description,pubDate,content);
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

    private String readPubDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "pubDate");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "pubDate");
        return summary;
    }

    private String readMediaContent(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "media:content");
        String summary = parser.getAttributeValue(null,"url");
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, null, "media:content");
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












}
