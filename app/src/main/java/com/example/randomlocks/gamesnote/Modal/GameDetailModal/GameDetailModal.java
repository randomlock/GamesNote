package com.example.randomlocks.gamesnote.Modal.GameDetailModal;

import com.example.randomlocks.gamesnote.Modal.GameCharacterModal.CharacterImage;
import com.example.randomlocks.gamesnote.Modal.GameWikiPlatform;
import com.google.gson.annotations.Expose;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by randomlocks on 5/29/2016.
 */
public class GameDetailModal extends RealmObject {

    public int api_url_key;
    @Expose
    public String description;
    @Expose
    public String name;
    @Expose
    public RealmList<GameWikiPlatform> platforms;
    @Expose
    public RealmList<CharacterImage> images;
    @Expose
    public RealmList<GameDetailVideo> videos;
    @Expose
    public RealmList<GameDetailCharacters> characters;
    @Expose
    public RealmList<GameDetailDevelopers> developers;
    @Expose
    public RealmList<GameDetailFranchises> franchises;
    @Expose
    public RealmList<GameDetailGenres> genres;
    @Expose
    public RealmList<GameDetailPublishers> publishers;
    @Expose
    public RealmList<GameDetailSimilarGames> similarGames;
    @Expose
    public RealmList<GameDetailThemes> themes;
    @Expose
    public RealmList<GameDetailReview> reviews;
    @Expose
    public RealmList<GameDetailReleases> releases;


}
