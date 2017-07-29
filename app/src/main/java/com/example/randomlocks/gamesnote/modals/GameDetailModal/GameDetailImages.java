package com.example.randomlocks.gamesnote.modals.GameDetailModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by randomlocks on 5/29/2016.
 */
public class GameDetailImages implements Parcelable {

    public static final Parcelable.Creator<GameDetailImages> CREATOR = new Parcelable.Creator<GameDetailImages>() {
        @Override
        public GameDetailImages createFromParcel(Parcel source) {
            return new GameDetailImages(source);
        }

        @Override
        public GameDetailImages[] newArray(int size) {
            return new GameDetailImages[size];
        }
    };
    @Expose
    public String thumbUrl;
    @Expose
    public String mediumUrl;

    public GameDetailImages() {
    }

    protected GameDetailImages(Parcel in) {
        this.thumbUrl = in.readString();
        this.mediumUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.thumbUrl);
        dest.writeString(this.mediumUrl);
    }
}
