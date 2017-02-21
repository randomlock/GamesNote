package com.example.randomlocks.gamesnote.Modal.GameDetailModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.randomlocks.gamesnote.Modal.GameCharacterModal.CharacterImage;
import com.example.randomlocks.gamesnote.Modal.GameWikiImage;
import com.example.randomlocks.gamesnote.Modal.GameWikiPlatform;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by randomlocks on 5/29/2016.
 */
public class GameDetailModal implements Parcelable {

    public int api_url_key;
    @Expose
    public String description;
    @Expose
    public String name;
    @Expose
    public List<GameWikiPlatform> platforms;
    @Expose
    public List<CharacterImage> images;
    @Expose
    public List<GameDetailVideo> videos;
    @Expose
    public List<GameDetailCharacters> characters;
    @Expose
    public List<GameDetailDevelopers> developers;
    @Expose
    public List<GameDetailFranchises> franchises;
    @Expose
    public List<GameDetailGenres> genres;
    @Expose
    public List<GameDetailPublishers> publishers;
    @Expose
    public List<GameDetailSimilarGames> similarGames;
    @Expose
    public List<GameDetailThemes> themes;
    @Expose
    public List<GameDetailReview> reviews;
    @Expose
    public List<GameDetailReleases> releases;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.api_url_key);
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

    public GameDetailModal() {
    }

    protected GameDetailModal(Parcel in) {
        this.api_url_key = in.readInt();
        this.description = in.readString();
        this.name = in.readString();
        this.platforms = in.createTypedArrayList(GameWikiPlatform.CREATOR);
        this.images = in.createTypedArrayList(CharacterImage.CREATOR);
        this.videos = new ArrayList<GameDetailVideo>();
        in.readList(this.videos, GameDetailVideo.class.getClassLoader());
        this.characters = in.createTypedArrayList(GameDetailCharacters.CREATOR);
        this.developers = in.createTypedArrayList(GameDetailDevelopers.CREATOR);
        this.franchises = in.createTypedArrayList(GameDetailFranchises.CREATOR);
        this.genres = in.createTypedArrayList(GameDetailGenres.CREATOR);
        this.publishers = new ArrayList<GameDetailPublishers>();
        in.readList(this.publishers, GameDetailPublishers.class.getClassLoader());
        this.similarGames = new ArrayList<GameDetailSimilarGames>();
        in.readList(this.similarGames, GameDetailSimilarGames.class.getClassLoader());
        this.themes = new ArrayList<GameDetailThemes>();
        in.readList(this.themes, GameDetailThemes.class.getClassLoader());
        this.reviews = new ArrayList<GameDetailReview>();
        in.readList(this.reviews, GameDetailReview.class.getClassLoader());
        this.releases = new ArrayList<GameDetailReleases>();
        in.readList(this.releases, GameDetailReleases.class.getClassLoader());
    }

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
}
