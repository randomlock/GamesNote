package com.example.randomlocks.gamesnote.Modal.GameDetailModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;

/**
 * Created by randomlocks on 5/29/2016.
 */
public class GameDetailThemes implements Parcelable {

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

    public GameDetailThemes() {
    }

    protected GameDetailThemes(Parcel in) {
        this.apiDetailUrl = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<GameDetailThemes> CREATOR = new Parcelable.Creator<GameDetailThemes>() {
        @Override
        public GameDetailThemes createFromParcel(Parcel source) {
            return new GameDetailThemes(source);
        }

        @Override
        public GameDetailThemes[] newArray(int size) {
            return new GameDetailThemes[size];
        }
    };
}
