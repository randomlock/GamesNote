package com.example.randomlocks.gamesnote.Activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.randomlocks.gamesnote.Adapter.GameDetailPagerAdapter;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.PagerDepthAnimation;
import com.example.randomlocks.gamesnote.R;

import java.util.ArrayList;

public class ViewPagerActivity extends AppCompatActivity {

    ViewPager viewPager;
    int position;
    ArrayList<String> imageUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        position = getIntent().getIntExtra(GiantBomb.POSITION, 0);
        imageUrls = getIntent().getStringArrayListExtra(GiantBomb.IMAGE_URL);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new GameDetailPagerAdapter(this, imageUrls.size(), imageUrls));
        viewPager.setPageTransformer(false, new PagerDepthAnimation());
        viewPager.setCurrentItem(position);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
