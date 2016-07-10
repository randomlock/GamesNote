package com.example.randomlocks.gamesnote.Modal.GameReviewModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by randomlocks on 7/7/2016.
 */
public class GameReviewModal implements Parcelable {

    @Expose
    public String deck;
    @Expose
    public String description;
    @Expose
    public String publishDate;
    @Expose
    public String reviewer;
    @Expose
    public int score;
    @Expose
    public String siteDetailUrl;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.deck);
        dest.writeString(this.description);
        dest.writeString(this.publishDate);
        dest.writeString(this.reviewer);
        dest.writeInt(this.score);
        dest.writeString(this.siteDetailUrl);
    }

    public GameReviewModal() {
    }

    protected GameReviewModal(Parcel in) {
        this.deck = in.readString();
        this.description = in.readString();
        this.publishDate = in.readString();
        this.reviewer = in.readString();
        this.score = in.readInt();
        this.siteDetailUrl = in.readString();
    }

    public static final Parcelable.Creator<GameReviewModal> CREATOR = new Parcelable.Creator<GameReviewModal>() {
        @Override
        public GameReviewModal createFromParcel(Parcel source) {
            return new GameReviewModal(source);
        }

        @Override
        public GameReviewModal[] newArray(int size) {
            return new GameReviewModal[size];
        }
    };
}
