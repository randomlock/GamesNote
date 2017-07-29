package com.example.randomlocks.gamesnote.modals.CharacterSearchModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.randomlocks.gamesnote.modals.GameCharacterModal.CharacterImage;
import com.example.randomlocks.gamesnote.modals.GameWikiImage;
import com.google.gson.annotations.Expose;

/**
 * Created by randomlocks on 7/11/2016.
 */
public class CharacterSearchModal implements Parcelable {

    public static final Creator<CharacterSearchModal> CREATOR = new Creator<CharacterSearchModal>() {
        @Override
        public CharacterSearchModal createFromParcel(Parcel in) {
            return new CharacterSearchModal(in);
        }

        @Override
        public CharacterSearchModal[] newArray(int size) {
            return new CharacterSearchModal[size];
        }
    };
    @Expose
    public String apiDetailUrl;
    @Expose
    public String name;
    @Expose
    public GameWikiImage image;
    @Expose
    public String deck;


    public CharacterSearchModal() {
    }

    protected CharacterSearchModal(Parcel in) {
        apiDetailUrl = in.readString();
        name = in.readString();
        image = in.readParcelable(CharacterImage.class.getClassLoader());
        deck = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(apiDetailUrl);
        parcel.writeString(name);
        parcel.writeParcelable(image, i);
        parcel.writeString(deck);
    }
}
