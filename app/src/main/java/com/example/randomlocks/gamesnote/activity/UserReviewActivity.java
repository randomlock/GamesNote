package com.example.randomlocks.gamesnote.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.adapter.UserReviewAdapter;
import com.example.randomlocks.gamesnote.helperClass.CustomView.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.helperClass.GiantBomb;
import com.example.randomlocks.gamesnote.helperClass.SharedPreference;
import com.example.randomlocks.gamesnote.interfaces.UserReviewInterface;
import com.example.randomlocks.gamesnote.modals.userReviewModal.UserReviewModal;
import com.example.randomlocks.gamesnote.modals.userReviewModal.UserReviewModalList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserReviewActivity extends AppCompatActivity {


    private static final String SCROLL_POSITION = "scroll_position";
    List<UserReviewModal> modalList = null;
    Toolbar toolbar;
    RecyclerView recyclerView;
    UserReviewAdapter adapter = null;
    AVLoadingIndicatorView pacman;
    CoordinatorLayout coordinatorLayout;
    TextView errorText;
    UserReviewInterface userReviewInterface = null;
    Map<String, String> map;
    String release_id = "object:3050-";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_review);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.root_coordinator);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        pacman = (AVLoadingIndicatorView) findViewById(R.id.progressBar);
        errorText = (TextView) findViewById(R.id.errortext);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("User Review");
        //not working
        //  getSupportActionBar().setTitle("xcvxcvxcv");

        if (savedInstanceState != null) {
            modalList = savedInstanceState.getParcelableArrayList(GiantBomb.MODAL);
            loadRecycler(modalList, savedInstanceState.getParcelable(SCROLL_POSITION));

        } else {

            if (modalList == null) {
                map = new HashMap<>();
                map.put(GiantBomb.KEY, SharedPreference.getFromSharedPreferences(GiantBomb.API_KEY, GiantBomb.DEFAULT_API_KEY, this));
                map.put(GiantBomb.FORMAT, "JSON");
                map.put(GiantBomb.FILTER, release_id + getIntent().getStringExtra(GiantBomb.REVIEW));
                userReviewInterface = GiantBomb.createUserReviewService();
                getUserReview(userReviewInterface, map);
            } else {
                loadRecycler(modalList, null);
            }

        }


    }

    private void loadRecycler(List<UserReviewModal> modalList, Parcelable parcelable) {

        if (pacman.getVisibility() == View.VISIBLE) {
            pacman.setVisibility(View.GONE);
        }
        if (errorText.getVisibility() == View.VISIBLE) {
            errorText.setVisibility(View.GONE);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (parcelable != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
        }
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new UserReviewAdapter(modalList, this));
    }

    private void getUserReview(final UserReviewInterface userReviewInterface, final Map<String, String> map) {

        pacman.setVisibility(View.VISIBLE);
        userReviewInterface.getResult(map).enqueue(new Callback<UserReviewModalList>() {
            @Override
            public void onResponse(Call<UserReviewModalList> call, Response<UserReviewModalList> response) {
                modalList = response.body().results;
                if (modalList.size() == 0) {
                    if (pacman.getVisibility() == View.VISIBLE) {
                        pacman.setVisibility(View.GONE);
                    }
                    errorText.setVisibility(View.VISIBLE);
                } else {
                    loadRecycler(modalList, null);

                }
            }

            @Override
            public void onFailure(Call<UserReviewModalList> call, Throwable t) {
                pacman.setVisibility(View.GONE);
                Snackbar.make(coordinatorLayout, "Connectivity Problem", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getUserReview(userReviewInterface, map);
                            }
                        }).show();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(GiantBomb.MODAL, new ArrayList<>(modalList));
        outState.putParcelable(SCROLL_POSITION, recyclerView.getLayoutManager().onSaveInstanceState());
    }
}
