package com.example.randomlocks.gamesnote.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.helperClass.CustomView.PhotoViewPager;
import com.example.randomlocks.gamesnote.helperClass.GiantBomb;
import com.example.randomlocks.gamesnote.helperClass.PagerDepthAnimation;
import com.example.randomlocks.gamesnote.helperClass.SharedPreference;
import com.example.randomlocks.gamesnote.helperClass.SingleMediaScanner;
import com.example.randomlocks.gamesnote.helperClass.Toaster;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewPagerActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    PhotoViewPager viewPager;
    Toolbar toolbar;
    int position;
    ArrayList<String> imageUrls;
    String gameTitle;
    String toolbar_title;
    boolean shouldFit = false;
    int option_id;
    ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewpager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }
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
        viewPager.setPageTransformer(false, new PagerDepthAnimation());
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(position);
            }
        });

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

            ivImage = (ImageView) viewPager.findViewWithTag(viewPager.getCurrentItem());
            option_id = item.getItemId();
            if (option_id == R.id.save) {
                checkForPermission(ivImage);
            } else {
                startAsyncTask();
            }

        }


        return true;

    }

    private void startAsyncTask() {
        if (ivImage != null && ivImage.getDrawable() != null) {
            new DownloadFileTask().execute(ivImage);
        } else {
            Toaster.make(this, "waiting to load image");
        }
    }

    private void shareImage(Uri bmpUri) {
        Log.d("FILE_PATH", bmpUri.toString());
        // Construct a ShareIntent with link to image
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setType("image/jpg");
        //shareIntent.setType("image/png");
        // Launch sharing dialog for image
        startActivity(Intent.createChooser(shareIntent, "Share image..."));


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
        File file = null;
        try {
            if (option_id == R.id.save) {
                File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                        + "/GamesNote/Pictures");

                // This location works best if you want the created images to be shared
                // between applications and persist after your app has been uninstalled.

                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        return null;
                    }
                }
                // Create a media file name
                String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm", Locale.getDefault()).format(new Date());
                String mImageName = gameTitle + timeStamp + ".jpg";
                file = new File(mediaStorageDir + File.separator + mImageName);
            } else {
                file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            }
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();

            if (option_id == R.id.save)
            new SingleMediaScanner(this, file);



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

    private void checkForPermission(ImageView ivImage) {
        int permission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            startAsyncTask();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startAsyncTask();

                } else {

                    Toaster.make(this, "Need read/write permission");
                }

        }
    }

    public void animateToolbar(){
        float alpha = toolbar.getAlpha();
        if (alpha == 1) {
            toolbar.animate().alpha(0).setDuration(200);
        } else {
            toolbar.animate().alpha(1).setDuration(200);

        }
    }

    private class DownloadFileTask extends AsyncTask<ImageView, Void, Uri> {

        @Override
        protected void onPreExecute() {
            if (option_id == R.id.save)
                Toasty.info(ImageViewPagerActivity.this, "saving...").show();
        }

        @Override
        protected Uri doInBackground(ImageView... imageViews) {
            return getLocalBitmapUri(imageViews[0]);
        }

        @Override
        protected void onPostExecute(Uri bmpUri) {



            switch (option_id) {


                case R.id.share:
                    shareImage(bmpUri);
                    break;


                case R.id.save:
                    Toasty.success(ImageViewPagerActivity.this, "image saved").show();
                    break;

                case R.id.gallery:
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(bmpUri, "image/*");
                    startActivity(intent);
                    break;

                case R.id.profile_image:
                    SharedPreference.saveToSharedPreference(GiantBomb.NAV_HEADER_URL, imageUrls.get(viewPager.getCurrentItem()), ImageViewPagerActivity.this);
                    Toasty.info(ImageViewPagerActivity.this, "profile photo will change after restart", Toast.LENGTH_LONG, true).show();
                    break;

            }

        }
    }

    //Adapter Class

    private class ImageViewerPagerAdapter extends PagerAdapter implements PhotoViewAttacher.OnViewTapListener {


        PhotoView imageView;
        ProgressBar progressBar;
        Context context;
        LayoutInflater layoutInflater;
        DisplayMetrics metrics;
        private Callback callback = new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {

            }
        };


        ImageViewerPagerAdapter(Context context) {
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
            container.removeView((FrameLayout) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            String imageUrl = imageUrls.get(position);

            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_viewpager, container, false);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

            imageView = (PhotoView) view.findViewById(R.id.image_preview);
            imageView.setTag(position);
            imageView.setOnViewTapListener(this);
          /*  final Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    imageView.setImageBitmap(bitmap);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    progressBar.setVisibility(View.VISIBLE);

                }
            };
            imageView.setTag(target);*/

            Picasso.with(context).load(imageUrl).resize(metrics.widthPixels, 0).into(imageView);





           /* if (shouldFit) {
                Picasso.with(context).load(imageUrl).fit().into(imageView,callback);
            } else {
                //   Picasso.with(context).load(imageUrl).resize(metrics.widthPixels, (int) ((int) (double)metrics.heightPixels/1.5)).centerCrop().into(imageView,callback);
                Picasso.with(context).load(imageUrl).into(imageView, callback);

            }*/


            container.addView(view);
            return view;
        }

        @Override
        public void onViewTap(View view, float v, float v1) {
            ((ImageViewPagerActivity)context).animateToolbar();
        }
    }




}
