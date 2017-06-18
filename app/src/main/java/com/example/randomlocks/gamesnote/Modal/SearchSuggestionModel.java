package com.example.randomlocks.gamesnote.Modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Created by randomlock on 6/18/2017.
 */

public class SearchSuggestionModel implements SearchSuggestion {

    private String suggestion;

    public SearchSuggestionModel(String suggestion) {
        this.suggestion = suggestion;
    }

    public SearchSuggestionModel() {
    }

    public SearchSuggestionModel(Parcel in) {
        this.suggestion = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.suggestion);
    }



    public static final Creator<SearchSuggestionModel> CREATOR = new Creator<SearchSuggestionModel>() {
        @Override
        public SearchSuggestionModel createFromParcel(Parcel source) {
            return new SearchSuggestionModel(source);
        }

        @Override
        public SearchSuggestionModel[] newArray(int size) {
            return new SearchSuggestionModel[size];
        }
    };


    @Override
    public String getBody() {
        return suggestion;
    }
}
