package com.example.randomlocks.gamesnote.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.Modal.GameCharacterModal.GameAppearance;
import com.example.randomlocks.gamesnote.R;

import java.util.ArrayList;
import java.util.List;

public class CharacterFriendEnemyActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Toolbar toolbar;
    ListView listView;
    ArrayAdapter<String> adapter;
    List<GameAppearance> modals;
    List<String> name;
    boolean isGameDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_friend_enemy);
        modals = getIntent().getParcelableArrayListExtra(GiantBomb.MODAL);
        isGameDetail = getIntent().getBooleanExtra(GiantBomb.IS_GAME_DETAIL,false);
        if (modals != null) {
            name = new ArrayList<>(modals.size());
            for (GameAppearance modal : modals) {
                name.add(modal.name);
            }
            toolbar = (Toolbar) findViewById(R.id.my_toolbar);
            listView = (ListView) findViewById(R.id.list_view);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, name);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (!isGameDetail) {
            Intent intent = new Intent(this, CharacterDetailActivity.class);
            intent.putExtra(GiantBomb.API_URL, modals.get(position).apiDetailUrl);
            startActivity(intent);
        }else {
            Intent it = new Intent(this, GameDetailActivity.class);
            it.putExtra("apiUrl", modals.get(position).apiDetailUrl);
            it.putExtra("name", modals.get(position).name);
            startActivity(it);


        }
    }
}
