package com.example.randomlocks.gamesnote.asyncTask;

import android.os.AsyncTask;

import com.example.randomlocks.gamesnote.modals.gameDetailModal.CharacterGamesImage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by randomlocks on 6/18/2016.
 */
public class JsoupGames extends AsyncTask<String, Void, List<CharacterGamesImage>> {


    public AsyncResponse delegate = null;

    public JsoupGames(AsyncResponse delegate) {
        this.delegate = delegate;

    }

    @Override
    protected List<CharacterGamesImage> doInBackground(String... params) {
        Document doc = null;
        Element element = null;
        List<CharacterGamesImage> list = new ArrayList<>();
        try {
            doc = Jsoup.connect(params[0])
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36")
                    .get();

            element = doc.body().getElementById("site-main").getElementById("mantle_skin")
                    .getElementById("wrapper").select("div.js-toc-generate").select("form.wikiGroup").first().getElementById("site")
                    .getElementById("default-content").select("div.primary-content.span8 ").first().select("div.js-table-pagintor-table").first().select("ul.editorial").first();

            Elements doclist = element.select("li.related-game");

            for (Element childList : doclist) {
                list.add(new CharacterGamesImage(childList.select("a").first().select("div.img.imgboxart.imgcast").select("img").attr("src"), childList.select("a").select("h3").text()));
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
    protected void onPostExecute(List<CharacterGamesImage> characterGamesImages) {
        delegate.processFinish(characterGamesImages);
    }


    public interface AsyncResponse {
        void processFinish(List<CharacterGamesImage> output);
    }
}
