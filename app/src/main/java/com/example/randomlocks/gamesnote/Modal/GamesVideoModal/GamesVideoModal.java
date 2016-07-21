package com.example.randomlocks.gamesnote.Modal.GamesVideoModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.randomlocks.gamesnote.Modal.GameCharacterModal.CharacterImage;
import com.google.gson.annotations.Expose;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by randomlocks on 7/18/2016.
 */
public class GamesVideoModal extends RealmObject implements Parcelable {
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
    public CharacterImage image;
    @Expose
    public String videoType;
    @Expose
    public String youtubeId;

    public static final int LIKE_TYPE = 1;
    public static final int WATCH_LATER_TYPE = 2;

    public boolean isFavorite = false;
    public boolean isWatchLater = false;



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
}
