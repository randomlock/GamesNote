package com.example.randomlocks.gamesnote.modals.gameCharacterModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by randomlocks on 6/25/2016.
 */
public class CharacterImage  implements Parcelable {

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
    @Expose
    public String mediumUrl;
    @Expose
    public String thumbUrl;

    public CharacterImage() {
    }


    public CharacterImage(String mediumUrl, String thumbUrl) {
        this.mediumUrl = mediumUrl;
        this.thumbUrl = thumbUrl;
    }

    protected CharacterImage(Parcel in) {
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
