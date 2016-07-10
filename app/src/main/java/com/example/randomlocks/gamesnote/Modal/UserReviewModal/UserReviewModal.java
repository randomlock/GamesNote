package com.example.randomlocks.gamesnote.Modal.UserReviewModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by randomlocks on 7/7/2016.
 */
public class UserReviewModal implements Parcelable {

    @Expose
    public String dateAdded;
    @Expose
    public String deck;
    @Expose
    public String description;
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
        dest.writeString(this.dateAdded);
        dest.writeString(this.deck);
        dest.writeString(this.description);
        dest.writeString(this.reviewer);
        dest.writeInt(this.score);
        dest.writeString(this.siteDetailUrl);
    }

    public UserReviewModal() {
    }

    protected UserReviewModal(Parcel in) {
        this.dateAdded = in.readString();
        this.deck = in.readString();
        this.description = in.readString();
        this.reviewer = in.readString();
        this.score = in.readInt();
        this.siteDetailUrl = in.readString();
    }

    public static final Parcelable.Creator<UserReviewModal> CREATOR = new Parcelable.Creator<UserReviewModal>() {
        @Override
        public UserReviewModal createFromParcel(Parcel source) {
            return new UserReviewModal(source);
        }

        @Override
        public UserReviewModal[] newArray(int size) {
            return new UserReviewModal[size];
        }
    };
}
