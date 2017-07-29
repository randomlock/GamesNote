package com.example.randomlocks.gamesnote.modals.GameDetailModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.randomlocks.gamesnote.modals.GameCharacterModal.CharacterImage;
import com.example.randomlocks.gamesnote.modals.GameWikiPlatform;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by randomlocks on 5/29/2016.
 */
public class GameDetailModal implements Parcelable {

    public static final Parcelable.Creator<GameDetailModal> CREATOR = new Parcelable.Creator<GameDetailModal>() {
        @Override
        public GameDetailModal createFromParcel(Parcel source) {
            return new GameDetailModal(source);
        }

        @Override
        public GameDetailModal[] newArray(int size) {
            return new GameDetailModal[size];
        }
    };
    @Expose
    public String description;
    @Expose
    public String name;
    @Expose
    public List<GameWikiPlatform> platforms;
    @Expose
    public int id;
    @Expose
    public List<CharacterImage> images;
    @Expose
    public List<GameDetailVideo> videos;
    @Expose
    public List<GameDetailIInnerJson> characters;
    @Expose
    public List<GameDetailIInnerJson> developers;
    @Expose
    public List<GameDetailIInnerJson> franchises;
    @Expose
    public List<GameDetailIInnerJson> genres;
    @Expose
    public List<GameDetailIInnerJson> publishers;
    @Expose
    public List<GameDetailIInnerJson> similarGames;
    @Expose
    public List<GameDetailIInnerJson> themes;
    @Expose
    public List<GameDetailIInnerJson> reviews;
    @Expose
    public List<GameDetailReleases> releases;

    public GameDetailModal() {
    }

    protected GameDetailModal(Parcel in) {
        this.description = in.readString();
        this.name = in.readString();
        this.platforms = in.createTypedArrayList(GameWikiPlatform.CREATOR);
        this.images = in.createTypedArrayList(CharacterImage.CREATOR);
        this.videos = new ArrayList<GameDetailVideo>();
        in.readList(this.videos, GameDetailVideo.class.getClassLoader());
        this.characters = in.createTypedArrayList(GameDetailIInnerJson.CREATOR);
        this.developers = in.createTypedArrayList(GameDetailIInnerJson.CREATOR);
        this.franchises = in.createTypedArrayList(GameDetailIInnerJson.CREATOR);
        this.genres = in.createTypedArrayList(GameDetailIInnerJson.CREATOR);
        this.publishers = new ArrayList<GameDetailIInnerJson>();
        in.readList(this.publishers, GameDetailIInnerJson.class.getClassLoader());
        this.similarGames = new ArrayList<GameDetailIInnerJson>();
        in.readList(this.similarGames, GameDetailIInnerJson.class.getClassLoader());
        this.themes = new ArrayList<GameDetailIInnerJson>();
        in.readList(this.themes, GameDetailIInnerJson.class.getClassLoader());
        this.reviews = new ArrayList<GameDetailIInnerJson>();
        in.readList(this.reviews, GameDetailIInnerJson.class.getClassLoader());
        this.releases = new ArrayList<GameDetailReleases>();
        in.readList(this.releases, GameDetailReleases.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.description);
        dest.writeString(this.name);
        dest.writeTypedList(this.platforms);
        dest.writeTypedList(this.images);
        dest.writeList(this.videos);
        dest.writeTypedList(this.characters);
        dest.writeTypedList(this.developers);
        dest.writeTypedList(this.franchises);
        dest.writeTypedList(this.genres);
        dest.writeList(this.publishers);
        dest.writeList(this.similarGames);
        dest.writeList(this.themes);
        dest.writeList(this.reviews);
        dest.writeList(this.releases);
    }
}
