package com.example.randomlocks.gamesnote.modals.GameDetailModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by randomlocks on 5/29/2016.
 */
public class GameDetailIInnerJson implements Parcelable {

    public static final Parcelable.Creator<GameDetailIInnerJson> CREATOR = new Parcelable.Creator<GameDetailIInnerJson>() {
        @Override
        public GameDetailIInnerJson createFromParcel(Parcel source) {
            return new GameDetailIInnerJson(source);
        }

        @Override
        public GameDetailIInnerJson[] newArray(int size) {
            return new GameDetailIInnerJson[size];
        }
    };
    @Expose
    public String apiDetailUrl;
    @Expose
    public String name;

    public GameDetailIInnerJson() {
    }

    private GameDetailIInnerJson(Parcel in) {
        this.apiDetailUrl = in.readString();
        this.name = in.readString();
    }

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
}
