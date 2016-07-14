package com.example.randomlocks.gamesnote.Modal.CharacterSearchModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.randomlocks.gamesnote.Modal.GameCharacterModal.CharacterImage;
import com.google.gson.annotations.Expose;

/**
 * Created by randomlocks on 7/11/2016.
 */
public class CharacterSearchModal implements Parcelable {

    @Expose
    public String apiDetailUrl;
    @Expose
    public String name;
    @Expose
    public CharacterImage image;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.apiDetailUrl);
        dest.writeString(this.name);
        dest.writeParcelable(this.image, flags);
    }

    public CharacterSearchModal() {
    }

    protected CharacterSearchModal(Parcel in) {
        this.apiDetailUrl = in.readString();
        this.name = in.readString();
        this.image = in.readParcelable(CharacterImage.class.getClassLoader());
    }

    public static final Parcelable.Creator<CharacterSearchModal> CREATOR = new Parcelable.Creator<CharacterSearchModal>() {
        @Override
        public CharacterSearchModal createFromParcel(Parcel source) {
            return new CharacterSearchModal(source);
        }

        @Override
        public CharacterSearchModal[] newArray(int size) {
            return new CharacterSearchModal[size];
        }
    };
}
