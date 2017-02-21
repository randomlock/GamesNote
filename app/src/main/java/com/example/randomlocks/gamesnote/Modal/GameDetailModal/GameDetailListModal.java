package com.example.randomlocks.gamesnote.Modal.GameDetailModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;

/**
 * Created by randomlocks on 5/29/2016.
 */
public class GameDetailListModal implements Parcelable {

    @Expose
    public GameDetailModal results;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.results, flags);
    }

    public GameDetailListModal() {
    }

    protected GameDetailListModal(Parcel in) {
        this.results = in.readParcelable(GameDetailModal.class.getClassLoader());
    }

    public static final Parcelable.Creator<GameDetailListModal> CREATOR = new Parcelable.Creator<GameDetailListModal>() {
        @Override
        public GameDetailListModal createFromParcel(Parcel source) {
            return new GameDetailListModal(source);
        }

        @Override
        public GameDetailListModal[] newArray(int size) {
            return new GameDetailListModal[size];
        }
    };
}
