package com.example.randomlocks.gamesnote.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.randomlocks.gamesnote.DialogFragment.ImageViewerFragment;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.Modal.BottomSheetImage;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

public class HdImageViewerActivity extends AppCompatActivity implements PhotoViewAttacher.OnViewTapListener {

    ImageView imageView;
    ProgressBar progressBar;
    String imageUrl;
    PhotoViewAttacher mAttacher;
    Toolbar toolbar;
    boolean isLoaded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hd_image_viewer);

        imageView = (ImageView) findViewById(R.id.hdimage);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imageUrl = getIntent().getStringExtra(ImageViewerFragment.MEDIUM_IMAGE_URL);


        if (imageUrl != null) {
            Picasso.with(this).load(imageUrl).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                    isLoaded = true;
                    mAttacher = new PhotoViewAttacher(imageView);
                    mAttacher.setOnViewTapListener(HdImageViewerActivity.this);

                }

                @Override
                public void onError() {
                    progressBar.setVisibility(View.GONE);
                    Toaster.make(HdImageViewerActivity.this, "cannot load image");
                }
            });
        }






                 /*   switch (i) {

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
                            Toaster.make(HdImageViewerActivity.this, "todo");
                            break;

                        case 3:
                            Toaster.make(HdImageViewerActivity.this, "todo");
                            break;


                    }*/





    }


    @Override
    public void onViewTap(View view, float v, float v1) {


        float alpha = toolbar.getAlpha();
        if (alpha == 1) {
            toolbar.animate().alpha(0).setDuration(200);
        } else {
            toolbar.animate().alpha(1).setDuration(200);

        }
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