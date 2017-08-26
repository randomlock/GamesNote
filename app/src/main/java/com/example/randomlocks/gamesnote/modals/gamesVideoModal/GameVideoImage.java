package com.example.randomlocks.gamesnote.modals.gamesVideoModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;

/**
 * Created by randomlock on 4/7/2017.
 */

public class GameVideoImage extends RealmObject implements Parcelable {

    public static final Parcelable.Creator<GameVideoImage> CREATOR = new Parcelable.Creator<GameVideoImage>() {
        @Override
        public GameVideoImage createFromParcel(Parcel source) {
            return new GameVideoImage(source);
        }

        @Override
        public GameVideoImage[] newArray(int size) {
            return new GameVideoImage[size];
        }
    };
    @Expose
    public String mediumUrl;
    @Expose
    public String thumbUrl;

    public GameVideoImage() {
    }


    public GameVideoImage(String mediumUrl, String thumbUrl) {
        this.mediumUrl = mediumUrl;
        this.thumbUrl = thumbUrl;
    }

    protected GameVideoImage(Parcel in) {
        this.mediumUrl = in.readString();
        this.thumbUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mediumUrl);
        dest.writeString(this.thumbUrl);
    }
}
