package com.example.randomlocks.gamesnote.Modal.GameDetailModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;

/**
 * Created by randomlocks on 5/29/2016.
 */
public class GameDetailGenres implements Parcelable {

    @Expose
    public String apiDetailUrl;
    @Expose
    public String name;


    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.apiDetailUrl);
        dest.writeString(this.name);
    }

    public GameDetailGenres() {
    }

    protected GameDetailGenres(Parcel in) {
        this.apiDetailUrl = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<GameDetailGenres> CREATOR = new Parcelable.Creator<GameDetailGenres>() {
        @Override
        public GameDetailGenres createFromParcel(Parcel source) {
            return new GameDetailGenres(source);
        }

        @Override
        public GameDetailGenres[] newArray(int size) {
            return new GameDetailGenres[size];
        }
    };
}
