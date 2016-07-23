package com.example.randomlocks.gamesnote.AsyncTask;

import android.os.AsyncTask;

import com.example.randomlocks.gamesnote.Modal.GameCharacterModal.CharacterImage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by randomlocks on 6/10/2016.
 */
public class JsoupCharacterWikiImage extends AsyncTask<String, Integer, List<CharacterImage>> {

    public interface AsyncResponse {
        void processFinish(List<CharacterImage> output);
    }

    public JsoupCharacterWikiImage(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    public AsyncResponse delegate = null;


    @Override
    protected List<CharacterImage> doInBackground(String... params) {
        Document doc = null;
        Element element = null;
        List<CharacterImage> imageUrl = null;
        try {
            doc = Jsoup.connect(params[0]).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").get();

            element = doc.body().getElementById("site-main").getElementById("mantle_skin")
                    .getElementById("wrapper").select("div.js-toc-generate").select("form.wikiGroup").first().getElementById("site")
                    .getElementById("default-content").select("aside.secondary-content.span4 ").first().select("div.gallery-box-pod").first();

            imageUrl = new ArrayList<>();

            Elements elements = element.select("a.imgflare");
            for (Element elem : elements) {
                imageUrl.add(new CharacterImage(elem.attr("href"), elem.select("img").attr("src")));
            }



        } catch (Exception e) {
            imageUrl = null;
        }


        return imageUrl;
    }


    @Override
    protected void onPostExecute(List<CharacterImage> imageUrl) {
        delegate.processFinish(imageUrl);
    }
}
