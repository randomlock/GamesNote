package com.example.randomlocks.gamesnote.Modal.GameDetailModal;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;

/**
 * Created by randomlocks on 5/29/2016.
 */
public class GameDetailGenres extends RealmObject{

   @Expose
   public String apiDetailUrl ;
   @Expose
   public String name;


   @Override
   public String toString() {
      return name;
   }
}
