package com.example.randomlocks.gamesnote.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by randomlocks on 4/9/2016.
 */
public class GameWikiModal implements Parcelable {

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
    @Expose
    public String apiDetailUrl;
    @Expose
    public String deck;
    @Expose
    public int expectedReleaseDay;
    @Expose
    public int expectedReleaseMonth;
    @Expose
    public int expectedReleaseYear;
    @Expose
    public int id;
    @Expose
    public GameWikiImage image;
    @Expose
    public String name;
    @Expose
    public String originalReleaseDate;
    @Expose
    public List<GameWikiPlatform> platforms;

    public GameWikiModal() {
    }

    protected GameWikiModal(Parcel in) {
        this.apiDetailUrl = in.readString();
        this.deck = in.readString();
        this.expectedReleaseDay = in.readInt();
        this.expectedReleaseMonth = in.readInt();
        this.expectedReleaseYear = in.readInt();
        this.image = in.readParcelable(GameWikiImage.class.getClassLoader());
        this.name = in.readString();
        this.originalReleaseDate = in.readString();
        this.platforms = in.createTypedArrayList(GameWikiPlatform.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.apiDetailUrl);
        dest.writeString(this.deck);
        dest.writeInt(this.expectedReleaseDay);
        dest.writeInt(this.expectedReleaseMonth);
        dest.writeInt(this.expectedReleaseYear);
        dest.writeParcelable(this.image, flags);
        dest.writeString(this.name);
        dest.writeString(this.originalReleaseDate);
        dest.writeTypedList(this.platforms);
    }
}
