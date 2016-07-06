package com.example.randomlocks.gamesnote.Modal.GameCharacterModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by randomlocks on 6/25/2016.
 */
public class GameAppearance implements Parcelable {

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

    public GameAppearance() {
    }

    protected GameAppearance(Parcel in) {
        this.apiDetailUrl = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<GameAppearance> CREATOR = new Parcelable.Creator<GameAppearance>() {
        @Override
        public GameAppearance createFromParcel(Parcel source) {
            return new GameAppearance(source);
        }

        @Override
        public GameAppearance[] newArray(int size) {
            return new GameAppearance[size];
        }
    };
}
