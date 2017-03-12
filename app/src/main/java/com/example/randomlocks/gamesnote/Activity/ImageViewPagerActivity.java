package com.example.randomlocks.gamesnote.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.randomlocks.gamesnote.HelperClass.CustomView.PhotoViewPager;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewPagerActivity extends AppCompatActivity {

    PhotoViewPager viewPager;
    Toolbar toolbar;
    int position;
    ArrayList<String> imageUrls;
    String gameTitle;
    String toolbar_title;
    boolean shouldFit = false;
    int option_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewpager);

        position = getIntent().getIntExtra(GiantBomb.POSITION, 0);
        imageUrls = getIntent().getStringArrayListExtra(GiantBomb.IMAGE_URL);
        gameTitle = getIntent().getStringExtra(GiantBomb.TITLE);
        if(gameTitle==null)
            gameTitle = "";

        viewPager = (PhotoViewPager) findViewById(R.id.viewpager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = "/"+imageUrls.size()+"  "+gameTitle;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle((position+1)+toolbar_title);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        viewPager.setAdapter(new ImageViewerPagerAdapter(this));
        //viewPager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(1);
       // viewPager.setPageTransformer(false, new PagerDepthAnimation());
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setTitle((position+1)+toolbar_title);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hdimage_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }else {

            ImageView ivImage = (ImageView) viewPager.findViewWithTag(GiantBomb.IMAGE_URL + viewPager.getCurrentItem());
            option_id = item.getItemId();
            new DownloadFileTask().execute(ivImage);
        }


        return true;

    }

    private class DownloadFileTask extends AsyncTask<ImageView,Void,Uri>{


        @Override
        protected Uri doInBackground(ImageView... imageViews) {
                return getLocalBitmapUri(imageViews[0]);
        }

        @Override
        protected void onPostExecute(Uri bmpUri) {

            switch (option_id){


                case R.id.share:
                    shareImage(bmpUri);
                    break;




                case R.id.save:
                    break;

                case R.id.gallery :
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(bmpUri,"image/*");
                    startActivity(intent);
                    break;

                case R.id.profile_image :
                    SharedPreference.saveToSharedPreference(GiantBomb.NAV_HEADER_URL,imageUrls.get(viewPager.getCurrentItem()),ImageViewPagerActivity.this);
                    Toasty.info(ImageViewPagerActivity.this,"profile photo will change after restart", Toast.LENGTH_LONG,true).show();

                    break;








            }

        }
    }


    private void shareImage(Uri bmpUri) {

        if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image*//**//*");
            //shareIntent.setType("image/png");
            // Launch sharing dialog for image
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } else {
            Toasty.error(this,"cannot share", Toast.LENGTH_SHORT,true).show();
        }


    }

    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        File file;
        try {

            file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                bmpUri = Uri.fromFile(file);
            }else {
                file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");

                bmpUri = FileProvider.getUriForFile(ImageViewPagerActivity.this, "com.codepath.fileprovider", file);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


    public void animateToolbar(){
        float alpha = toolbar.getAlpha();
        if (alpha == 1) {
            toolbar.animate().alpha(0).setDuration(200);
        } else {
            toolbar.animate().alpha(1).setDuration(200);

        }
    }

    //Adapter Class

    class ImageViewerPagerAdapter extends PagerAdapter implements PhotoViewAttacher.OnViewTapListener {


        PhotoViewAttacher mAttacher;
        PhotoView imageView;
        Context context;
        LayoutInflater layoutInflater;
        DisplayMetrics metrics;

        public ImageViewerPagerAdapter(Context context) {
            this.context = context;
             metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
        }


        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            String imageUrl = imageUrls.get(position);

            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_viewpager, container, false);

            imageView = (PhotoView) view.findViewById(R.id.image_preview);
            imageView.setTag(GiantBomb.IMAGE_URL+ position);










            mAttacher = new PhotoViewAttacher(imageView);
            mAttacher.setOnViewTapListener(this);

            if (shouldFit) {
                Picasso.with(context).load(imageUrl).into(imageView,callback);
            } else {
                Toaster.make(context,"hello");
                Picasso.with(context).load(imageUrl).resize(metrics.widthPixels, (int) ((int) (double)metrics.heightPixels/1.5)).centerCrop().into(imageView,callback);
            }

            container.addView(view);


            return view;
        }

        private Callback callback = new Callback() {
            @Override
            public void onSuccess() {
                mAttacher.update();
            }

            @Override
            public void onError() {

            }
        };


        @Override
        public void onViewTap(View view, float v, float v1) {
            ((ImageViewPagerActivity)context).animateToolbar();
        }
    }




}
