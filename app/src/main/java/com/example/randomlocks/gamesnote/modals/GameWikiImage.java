package com.example.randomlocks.gamesnote.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by randomlocks on 4/25/2016.
 */
public class GameWikiImage implements Parcelable {

    public static final Parcelable.Creator<GameWikiImage> CREATOR = new Parcelable.Creator<GameWikiImage>() {
        @Override
        public GameWikiImage createFromParcel(Parcel source) {
            return new GameWikiImage(source);
        }

        @Override
        public GameWikiImage[] newArray(int size) {
            return new GameWikiImage[size];
        }
    };
    @Expose
    public String thumbUrl;
    @Expose
    public String mediumUrl;
    @Expose
    public String smallUrl;

    public GameWikiImage() {
    }

    private GameWikiImage(Parcel in) {
        this.thumbUrl = in.readString();
        this.mediumUrl = in.readString();
        this.smallUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.thumbUrl);
        dest.writeString(this.mediumUrl);
        dest.writeString(this.smallUrl);
    }
}
