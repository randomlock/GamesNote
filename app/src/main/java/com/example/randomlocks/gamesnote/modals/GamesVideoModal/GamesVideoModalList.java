package com.example.randomlocks.gamesnote.modals.GamesVideoModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by randomlocks on 7/18/2016.
 */
public class GamesVideoModalList implements Parcelable {

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
    @Expose
    public int numberOfTotalResults;
    @Expose
    public List<GamesVideoModal> results;


    public GamesVideoModalList() {
    }

    protected GamesVideoModalList(Parcel in) {
        this.numberOfTotalResults = in.readInt();
        this.results = in.createTypedArrayList(GamesVideoModal.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.numberOfTotalResults);
        dest.writeTypedList(this.results);
    }
}
