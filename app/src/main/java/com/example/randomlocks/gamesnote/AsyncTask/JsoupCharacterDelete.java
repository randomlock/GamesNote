package com.example.randomlocks.gamesnote.AsyncTask;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by randomlocks on 6/10/2016.
 */
public class JsoupCharacterDelete extends AsyncTask<String, Integer, Elements> {

    public interface AsyncResponse {
        void processFinish(Elements output);
    }

    public JsoupCharacterDelete(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    public AsyncResponse delegate = null;


    @Override
    protected Elements doInBackground(String... params) {
        Document doc = null;
        Elements element = null;
        try {
            doc = Jsoup.connect(params[0]).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").get();

            element = doc.body().getElementById("site-main").getElementById("mantle_skin")
                    .getElementById("wrapper").select("div.js-toc-generate").select("form.wikiGroup").first().getElementById("site")
                    .getElementById("default-content").select("aside.secondary-content.span4 ").first().select("div.tabbable");


        } catch (Exception e) {
            element = null;
        }


        return element;
    }


    @Override
    protected void onPostExecute(Elements elements) {
        delegate.processFinish(elements);
    }
}
