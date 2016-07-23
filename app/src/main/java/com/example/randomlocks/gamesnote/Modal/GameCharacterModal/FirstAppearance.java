package com.example.randomlocks.gamesnote.Modal.GameCharacterModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by randomlocks on 6/25/2016.
 */
public class FirstAppearance implements Parcelable {

    @Expose
    public String apiDetailUrl;
    @Expose
    public String name;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.apiDetailUrl);
        dest.writeString(this.name);
    }

    public FirstAppearance() {
    }

    protected FirstAppearance(Parcel in) {
        this.apiDetailUrl = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<FirstAppearance> CREATOR = new Parcelable.Creator<FirstAppearance>() {
        @Override
        public FirstAppearance createFromParcel(Parcel source) {
            return new FirstAppearance(source);
        }

        @Override
        public FirstAppearance[] newArray(int size) {
            return new FirstAppearance[size];
        }
    };
}
