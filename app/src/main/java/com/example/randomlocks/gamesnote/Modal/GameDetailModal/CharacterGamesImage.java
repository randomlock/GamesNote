package com.example.randomlocks.gamesnote.Modal.GameDetailModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;

/**
 * Created by randomlocks on 6/11/2016.
 */
public class CharacterGamesImage implements Parcelable {

    @Expose
    public String imageUrl;
    @Expose
    public String name;


    public CharacterGamesImage(String imageUrl, String name) {
        this.imageUrl = imageUrl;
        this.name = name;
    }

    public CharacterGamesImage() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageUrl);
        dest.writeString(this.name);
    }

    protected CharacterGamesImage(Parcel in) {
        this.imageUrl = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<CharacterGamesImage> CREATOR = new Parcelable.Creator<CharacterGamesImage>() {
        @Override
        public CharacterGamesImage createFromParcel(Parcel source) {
            return new CharacterGamesImage(source);
        }

        @Override
        public CharacterGamesImage[] newArray(int size) {
            return new CharacterGamesImage[size];
        }
    };
}
