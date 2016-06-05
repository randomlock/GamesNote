package com.example.randomlocks.gamesnote.Modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by randomlocks on 4/9/2016.
 */
public class GameWikiModal implements Parcelable {

    public String apiDetailUrl;
    public String deck;
    public String description;
    public int expectedReleaseDay;
    public int expectedReleaseMonth;
    public int expectedReleaseYear;
    public GameWikiImage image;
    public String name;
    public String originalReleaseDate;
    public List<GameWikiPlatform> platforms;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.apiDetailUrl);
        dest.writeString(this.deck);
        dest.writeString(this.description);
        dest.writeInt(this.expectedReleaseDay);
        dest.writeInt(this.expectedReleaseMonth);
        dest.writeInt(this.expectedReleaseYear);
        dest.writeParcelable(this.image, flags);
        dest.writeString(this.name);
        dest.writeString(this.originalReleaseDate);
        dest.writeTypedList(this.platforms);
    }

    public GameWikiModal() {
    }

    protected GameWikiModal(Parcel in) {
        this.apiDetailUrl = in.readString();
        this.deck = in.readString();
        this.description = in.readString();
        this.expectedReleaseDay = in.readInt();
        this.expectedReleaseMonth = in.readInt();
        this.expectedReleaseYear = in.readInt();
        this.image = in.readParcelable(GameWikiImage.class.getClassLoader());
        this.name = in.readString();
        this.originalReleaseDate = in.readString();
        this.platforms = in.createTypedArrayList(GameWikiPlatform.CREATOR);
    }

    public static final Parcelable.Creator<GameWikiModal> CREATOR = new Parcelable.Creator<GameWikiModal>() {
        @Override
        public GameWikiModal createFromParcel(Parcel source) {
            return new GameWikiModal(source);
        }

        @Override
        public GameWikiModal[] newArray(int size) {
            return new GameWikiModal[size];
        }
    };
}
