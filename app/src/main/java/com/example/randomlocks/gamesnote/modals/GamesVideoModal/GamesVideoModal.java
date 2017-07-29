package com.example.randomlocks.gamesnote.modals.GamesVideoModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.randomlocks.gamesnote.modals.GameCharacterModal.CharacterImage;
import com.example.randomlocks.gamesnote.modals.GameDetailModal.GameVideoModalMinimal;
import com.google.gson.annotations.Expose;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class GamesVideoModal extends RealmObject implements Parcelable {


    public static final Parcelable.Creator<GamesVideoModal> CREATOR = new Parcelable.Creator<GamesVideoModal>() {
        @Override
        public GamesVideoModal createFromParcel(Parcel source) {
            return new GamesVideoModal(source);
        }

        @Override
        public GamesVideoModal[] newArray(int size) {
            return new GamesVideoModal[size];
        }
    };
    @Expose
    public String deck;
    @Expose
    public String highUrl;
    @Expose
    public String lowUrl;
    @Expose
    @PrimaryKey
    public int id;
    @Expose
    public int lengthSeconds;
    @Expose
    public String name;
    @Expose
    public String publishDate;
    @Expose
    public String siteDetailUrl;
    @Expose
    public GameVideoImage image;
    @Expose
    public String videoType;
    @Expose
    public String youtubeId;
    public boolean isFavorite = false;
    public boolean isWatchLater = false;
    public Date dateAdded;




    public GamesVideoModal(GameVideoModalMinimal modalMinimal) {
        this.deck = "";
        this.highUrl = modalMinimal.highUrl;
        this.lowUrl = modalMinimal.lowUrl;
        this.id = 4;
        this.lengthSeconds = modalMinimal.lengthSeconds;
        this.name = modalMinimal.name;
        this.publishDate = "";
        this.siteDetailUrl = modalMinimal.siteDetailUrl;
        this.image = modalMinimal.image;
        this.videoType = "";
        this.youtubeId = modalMinimal.youtubeId;
    }

    public GamesVideoModal() {
    }

    protected GamesVideoModal(Parcel in) {
        this.deck = in.readString();
        this.highUrl = in.readString();
        this.lowUrl = in.readString();
        this.id = in.readInt();
        this.lengthSeconds = in.readInt();
        this.name = in.readString();
        this.publishDate = in.readString();
        this.siteDetailUrl = in.readString();
        this.image = in.readParcelable(CharacterImage.class.getClassLoader());
        this.videoType = in.readString();
        this.youtubeId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.deck);
        dest.writeString(this.highUrl);
        dest.writeString(this.lowUrl);
        dest.writeInt(this.id);
        dest.writeInt(this.lengthSeconds);
        dest.writeString(this.name);
        dest.writeString(this.publishDate);
        dest.writeString(this.siteDetailUrl);
        dest.writeParcelable(this.image, flags);
        dest.writeString(this.videoType);
        dest.writeString(this.youtubeId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GamesVideoModal modal = (GamesVideoModal) o;

        return id == modal.id;

    }

    @Override
    public int hashCode() {
        return id;
    }


}
