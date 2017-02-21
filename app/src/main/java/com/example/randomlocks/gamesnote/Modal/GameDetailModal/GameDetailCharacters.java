package com.example.randomlocks.gamesnote.Modal.GameDetailModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;

/**
 * Created by randomlocks on 5/29/2016.
 */
public class GameDetailCharacters implements Parcelable {

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

    public GameDetailCharacters() {
    }

    protected GameDetailCharacters(Parcel in) {
        this.apiDetailUrl = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<GameDetailCharacters> CREATOR = new Parcelable.Creator<GameDetailCharacters>() {
        @Override
        public GameDetailCharacters createFromParcel(Parcel source) {
            return new GameDetailCharacters(source);
        }

        @Override
        public GameDetailCharacters[] newArray(int size) {
            return new GameDetailCharacters[size];
        }
    };
}
