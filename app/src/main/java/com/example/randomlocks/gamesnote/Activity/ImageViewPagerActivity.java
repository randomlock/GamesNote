package com.example.randomlocks.gamesnote.Activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.randomlocks.gamesnote.Adapter.ImageViewerPagerAdapter;
import com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment.ImagePagerFragment;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.Modal.BottomSheetImage;
import com.example.randomlocks.gamesnote.R;

import java.util.ArrayList;

public class ImageViewPagerActivity extends AppCompatActivity {

    ViewPager viewPager;
    Toolbar toolbar;
    int position;
    ArrayList<String> imageUrls;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        position = getIntent().getIntExtra(GiantBomb.POSITION, 0);
        imageUrls = getIntent().getStringArrayListExtra(GiantBomb.IMAGE_URL);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        viewPager.setAdapter(new ImageViewerPagerAdapter(this, imageUrls.size(), imageUrls, false));
        //viewPager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(1);
       // viewPager.setPageTransformer(false, new PagerDepthAnimation());
        viewPager.setCurrentItem(position);





               /* if (isLoaded) {

                    Toaster.make(ImageViewPagerActivity.this, i + "");
                    switch (i) {

                        //share
                        case 0:

                            File myFile = new File(imageUrl);
                            MimeTypeMap mime = MimeTypeMap.getSingleton();
                            String ext = myFile.getName().substring(myFile.getName().lastIndexOf(".") + 1);
                            String type = mime.getMimeTypeFromExtension(ext);
                            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                            sharingIntent.setType(type);
                            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(myFile));
                            startActivity(Intent.createChooser(sharingIntent, "Share using"));
                            break;

                        case 1:
                            Toaster.make(ImageViewPagerActivity.this, "todo");
                            break;

                        case 3:
                            Toaster.make(ImageViewPagerActivity.this, "todo");
                            break;


                    }
                }*/







    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hdimage_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.share:
                break;

            case R.id.save:
                break;




        }

        return true;
    }




}
