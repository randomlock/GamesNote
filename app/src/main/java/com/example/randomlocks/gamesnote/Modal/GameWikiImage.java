package com.example.randomlocks.gamesnote.Modal;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by randomlocks on 4/25/2016.
 */
public class GameWikiImage implements Parcelable {


    public String iconUrl;
    public String mediumUrl;
    public String smallUrl;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.iconUrl);
        dest.writeString(this.mediumUrl);
        dest.writeString(this.smallUrl);
    }

    public GameWikiImage() {
    }

    protected GameWikiImage(Parcel in) {
        this.iconUrl = in.readString();
        this.mediumUrl = in.readString();
        this.smallUrl = in.readString();
    }

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
}
