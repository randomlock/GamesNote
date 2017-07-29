package com.example.randomlocks.gamesnote.modals.GameDetailModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by randomlocks on 7/24/2016.
 */
public class GameDetailVideo implements Parcelable {

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
    @Expose
    public String apiDetailUrl;
    @Expose
    public String name;
    @Expose
    public String siteDetailUrl;

    public GameDetailVideo() {
    }

    protected GameDetailVideo(Parcel in) {
        this.apiDetailUrl = in.readString();
        this.name = in.readString();
        this.siteDetailUrl = in.readString();
    }

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
}
