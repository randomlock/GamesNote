package com.example.randomlocks.gamesnote.asyncTask;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;


public class JsoupHltbStats extends AsyncTask<String, Integer, ArrayList<String>> {


    private AsyncResponse delegate = null;
    private ArrayList<String> key = new ArrayList<>();


    public JsoupHltbStats(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        Document doc = null;
        Element element = null;
        ArrayList<String> value = new ArrayList<>();
        try {
            doc = Jsoup.connect(params[0])
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                    .data("queryString", params[1])
                    .post();

            element = doc.body().getElementsByClass("search_list_details").first();
            Elements elements = element.getElementsByClass("search_list_tidbit");
            int i = 0, size = elements.size();
            while (i + 1 < size) {
                key.add(elements.get(i).text());
                value.add(elements.get(i + 1).text());
                i += 2;
            }

        } catch (Exception e) {
            key = null;
            value = null;
        }


        return value;
    }

    @Override
    protected void onPostExecute(ArrayList<String> value) {
        delegate.processFinish(key, value);
    }

    public interface AsyncResponse {
        void processFinish(ArrayList<String> key, ArrayList<String> output);
    }
}
