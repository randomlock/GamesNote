package com.example.randomlocks.gamesnote.Modal.GameDetailModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;

/**
 * Created by randomlocks on 7/24/2016.
 */
public class GameDetailVideo implements Parcelable {

    @Expose
    public String apiDetailUrl;
    @Expose
    public String name;
    @Expose
    public String siteDetailUrl;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.apiDetailUrl);
        dest.writeString(this.name);
        dest.writeString(this.siteDetailUrl);
    }

    public GameDetailVideo() {
    }

    protected GameDetailVideo(Parcel in) {
        this.apiDetailUrl = in.readString();
        this.name = in.readString();
        this.siteDetailUrl = in.readString();
    }

    public static final Parcelable.Creator<GameDetailVideo> CREATOR = new Parcelable.Creator<GameDetailVideo>() {
        @Override
        public GameDetailVideo createFromParcel(Parcel source) {
            return new GameDetailVideo(source);
        }

        @Override
        public GameDetailVideo[] newArray(int size) {
            return new GameDetailVideo[size];
        }
    };
}
