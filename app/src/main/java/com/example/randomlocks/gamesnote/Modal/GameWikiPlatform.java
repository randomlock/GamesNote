package com.example.randomlocks.gamesnote.Modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by randomlocks on 4/25/2016.
 */
public class GameWikiPlatform implements Parcelable {

    @Expose
    public String name;
    @Expose
    public String abbreviation;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.abbreviation);
    }

    public GameWikiPlatform() {
    }

    protected GameWikiPlatform(Parcel in) {
        this.name = in.readString();
        this.abbreviation = in.readString();
    }

    public static final Parcelable.Creator<GameWikiPlatform> CREATOR = new Parcelable.Creator<GameWikiPlatform>() {
        @Override
        public GameWikiPlatform createFromParcel(Parcel source) {
            return new GameWikiPlatform(source);
        }

        @Override
        public GameWikiPlatform[] newArray(int size) {
            return new GameWikiPlatform[size];
        }
    };
}
