package com.example.randomlocks.gamesnote.realmDatabase;

import android.text.TextUtils;

import com.example.randomlocks.gamesnote.modals.SearchSuggestionModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.Index;

/**
 * Created by randomlock on 6/18/2017.
 */

public class SearchHistoryDatabase extends RealmObject {

    public static final int CHARACTER_WIKI = 0;
    public static final int GAME_WIKI = 1;
    public static final int VIDEO_WIKI = 2;
    public static String TITLE = "title";
    public static String SEARCH_TYPE = "search_type";
    public static String DATE_ADDED = "date_added";
    @Index
    private int search_type;
    @Index
    private String title;
    private Date date_added;

    public SearchHistoryDatabase(int search_type, String title) {
        this.search_type = search_type;
        this.title = title;
        date_added = new Date();
    }

    public SearchHistoryDatabase() {
    }

    public static RealmResults<SearchHistoryDatabase> search(Realm realm, String query,int search_type,boolean partialSearch){
        RealmResults<SearchHistoryDatabase> realmResults = realm.where(SearchHistoryDatabase.class).findAll();
        if (TextUtils.isEmpty(query)) {
            return realmResults;
        }
        String[] keywords = query.split(" ");
        for (String keyword : keywords) {
            String spacedKeyword = " " + keyword;
            realmResults = realmResults.where().beginsWith(TITLE, keyword, Case.INSENSITIVE).or().contains(TITLE, spacedKeyword, Case.SENSITIVE).equalTo(SEARCH_TYPE,search_type).findAll();
        }
        return realmResults;
    }

    public static  List<SearchSuggestionModel> getHistory(Realm realm,int search_type,int limit){
        RealmResults<SearchHistoryDatabase> realmResults = realm.where(SearchHistoryDatabase.class).equalTo(SEARCH_TYPE,search_type).findAllSorted(DATE_ADDED, Sort.DESCENDING);
        List<SearchSuggestionModel> result = new ArrayList<>(limit);
        int i = 0;
        for(SearchHistoryDatabase realmResult : realmResults){
            result.add(new SearchSuggestionModel(realmResult.getTitle()));
            i++;
            if(i>=limit)
                break;
        }
        return result;
    }

    public int getSearch_type() {
        return search_type;
    }

    public void setSearch_type(int search_type) {
        this.search_type = search_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCurrent_date() {
        return date_added;
    }

    public void setDate_added(Date date_added) {
        this.date_added = date_added;
    }

    public interface OnPerformSearchListener {
        public void onPerform();
    }
}
