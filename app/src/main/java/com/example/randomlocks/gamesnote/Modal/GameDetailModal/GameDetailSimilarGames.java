package com.example.randomlocks.gamesnote.Modal.GameDetailModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;

/**
 * Created by randomlocks on 5/29/2016.
 */
public class GameDetailSimilarGames implements Parcelable {

    @Expose
    public String apiDetailUrl;
    @Expose
    public String name;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.apiDetailUrl);
        dest.writeString(this.name);
    }

    public GameDetailSimilarGames() {
    }

    protected GameDetailSimilarGames(Parcel in) {
        this.apiDetailUrl = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<GameDetailSimilarGames> CREATOR = new Parcelable.Creator<GameDetailSimilarGames>() {
        @Override
        public GameDetailSimilarGames createFromParcel(Parcel source) {
            return new GameDetailSimilarGames(source);
        }

        @Override
        public GameDetailSimilarGames[] newArray(int size) {
            return new GameDetailSimilarGames[size];
        }
    };
}
