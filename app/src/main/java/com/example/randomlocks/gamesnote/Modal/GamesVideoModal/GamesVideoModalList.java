package com.example.randomlocks.gamesnote.Modal.GamesVideoModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by randomlocks on 7/18/2016.
 */
public class GamesVideoModalList implements Parcelable {

    @Expose
    public List<GamesVideoModal> results;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.results);
    }

    public GamesVideoModalList() {
    }

    protected GamesVideoModalList(Parcel in) {
        this.results = in.createTypedArrayList(GamesVideoModal.CREATOR);
    }

    public static final Parcelable.Creator<GamesVideoModalList> CREATOR = new Parcelable.Creator<GamesVideoModalList>() {
        @Override
        public GamesVideoModalList createFromParcel(Parcel source) {
            return new GamesVideoModalList(source);
        }

        @Override
        public GamesVideoModalList[] newArray(int size) {
            return new GamesVideoModalList[size];
        }
    };
}
