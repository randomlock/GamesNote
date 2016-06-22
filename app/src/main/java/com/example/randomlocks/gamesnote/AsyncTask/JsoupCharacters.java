package com.example.randomlocks.gamesnote.AsyncTask;

import android.os.AsyncTask;

import com.example.randomlocks.gamesnote.Modal.GameDetailModal.CharacterGamesImage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by randomlocks on 6/18/2016.
 */
public class JsoupCharacters extends AsyncTask<String,Integer,List<CharacterGamesImage>> {


    public interface AsyncResponse {
        void processFinish(List<CharacterGamesImage> output);
    }

    public JsoupCharacters(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    public AsyncResponse delegate = null;


    @Override
    protected List<CharacterGamesImage> doInBackground(String... params) {
        Document doc = null;
        Element element = null;
        List<CharacterGamesImage> list = new ArrayList<>();
        try {
            doc = Jsoup.connect(params[0])
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                    .get();

            element = doc.body().getElementById("site-main").getElementById("mantle_skin")
                    .getElementById("wrapper").select("div.js-toc-generate").select("form.wikiGroup").first().getElementById("site")
                    .getElementById("default-content").select("div.primary-content.span8 ").first().select("div.js-table-pagintor-table").first().select("ul.editorial").first();

            Elements doclist = element.getElementsByTag("li");

            for (Element childList : doclist){
                list.add(new CharacterGamesImage(childList.select("div.img.imgboxart.imgcast").select("img").attr("src"),childList.select("h3").text()));
            }

            Collections.sort(list, new Comparator<CharacterGamesImage>() {
                @Override
                public int compare(CharacterGamesImage lhs, CharacterGamesImage rhs) {
                    return lhs.name.compareTo(rhs.name);
                }
            });



        } catch (Exception e) {
            list = null;
        }



        return list;
    }

    @Override
    protected void onPostExecute(List<CharacterGamesImage> strings) {
        delegate.processFinish(strings);
    }
}
