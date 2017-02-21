package com.example.randomlocks.gamesnote.Modal.GameDetailModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;

/**
 * Created by randomlocks on 7/7/2016.
 */
public class GameDetailReview implements Parcelable {

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

    public GameDetailReview() {
    }

    protected GameDetailReview(Parcel in) {
        this.apiDetailUrl = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<GameDetailReview> CREATOR = new Parcelable.Creator<GameDetailReview>() {
        @Override
        public GameDetailReview createFromParcel(Parcel source) {
            return new GameDetailReview(source);
        }

        @Override
        public GameDetailReview[] newArray(int size) {
            return new GameDetailReview[size];
        }
    };
}
