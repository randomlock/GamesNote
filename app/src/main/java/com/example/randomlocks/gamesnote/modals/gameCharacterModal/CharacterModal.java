package com.example.randomlocks.gamesnote.modals.gameCharacterModal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by randomlocks on 6/25/2016.
 */
public class CharacterModal implements Parcelable {


    public static final Parcelable.Creator<CharacterModal> CREATOR = new Parcelable.Creator<CharacterModal>() {
        @Override
        public CharacterModal createFromParcel(Parcel source) {
            return new CharacterModal(source);
        }

        @Override
        public CharacterModal[] newArray(int size) {
            return new CharacterModal[size];
        }
    };
    @Expose
    public String birthday;
    @Expose
    public String aliases;
    @Expose
    public String deck;
    @Expose
    public String description;
    @Expose
    public FirstAppearance firstAppearedInGame;
    @Expose
    public List<GameAppearance> enemies;
    @Expose
    public List<GameAppearance> friends;
    @Expose
    public List<GameAppearance> games;
    @Expose
    public int gender;
    @Expose
    public CharacterImage image;
    @Expose
    public String name;


    public CharacterModal() {
    }

    protected CharacterModal(Parcel in) {
        this.birthday = in.readString();
        this.aliases = in.readString();
        this.deck = in.readString();
        this.description = in.readString();
        this.firstAppearedInGame = in.readParcelable(FirstAppearance.class.getClassLoader());
        this.enemies = in.createTypedArrayList(GameAppearance.CREATOR);
        this.friends = in.createTypedArrayList(GameAppearance.CREATOR);
        this.games = in.createTypedArrayList(GameAppearance.CREATOR);
        this.gender = in.readInt();
        this.image = in.readParcelable(CharacterImage.class.getClassLoader());
        this.name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.birthday);
        dest.writeString(this.aliases);
        dest.writeString(this.deck);
        dest.writeString(this.description);
        dest.writeParcelable(this.firstAppearedInGame, flags);
        dest.writeTypedList(this.enemies);
        dest.writeTypedList(this.friends);
        dest.writeTypedList(this.games);
        dest.writeInt(this.gender);
        dest.writeParcelable(this.image, flags);
        dest.writeString(this.name);
    }
}
