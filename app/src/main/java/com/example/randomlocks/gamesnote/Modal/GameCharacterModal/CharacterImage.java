package com.example.randomlocks.gamesnote.Modal.GameCharacterModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by randomlocks on 6/25/2016.
 */
public class CharacterImage implements Parcelable {

    @Expose
   public String mediumUrl;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mediumUrl);
    }

    public CharacterImage() {
    }

    protected CharacterImage(Parcel in) {
        this.mediumUrl = in.readString();
    }

    public static final Parcelable.Creator<CharacterImage> CREATOR = new Parcelable.Creator<CharacterImage>() {
        @Override
        public CharacterImage createFromParcel(Parcel source) {
            return new CharacterImage(source);
        }

        @Override
        public CharacterImage[] newArray(int size) {
            return new CharacterImage[size];
        }
    };
}