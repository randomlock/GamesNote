package com.example.randomlocks.gamesnote.modals.gameDetailModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by randomlocks on 7/7/2016.
 */
public class GameDetailReleases implements Parcelable {


    public static final Parcelable.Creator<GameDetailReleases> CREATOR = new Parcelable.Creator<GameDetailReleases>() {
        @Override
        public GameDetailReleases createFromParcel(Parcel source) {
            return new GameDetailReleases(source);
        }

        @Override
        public GameDetailReleases[] newArray(int size) {
            return new GameDetailReleases[size];
        }
    };
    @Expose
    public String id;
    @Expose
    public String name;

    public GameDetailReleases() {
    }

    protected GameDetailReleases(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
    }
}
