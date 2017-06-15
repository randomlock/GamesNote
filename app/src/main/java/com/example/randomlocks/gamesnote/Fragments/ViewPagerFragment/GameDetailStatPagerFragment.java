package com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.randomlocks.gamesnote.Adapter.GameStatsAdapter;
import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.RealmDatabase.GameDetailDatabase;
import com.example.randomlocks.gamesnote.RealmDatabase.GameListDatabase;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.example.randomlocks.gamesnote.R.string.List;

/**
 * Created by randomlock on 6/5/2017.
 */

public class GameDetailStatPagerFragment extends Fragment implements NestedScrollView.OnScrollChangeListener{


    private Realm realm;
    private RealmResults<GameDetailDatabase> result;
    int count;
    int rainbow[],new_rainbow[];
    NestedScrollView nestedScrollView;
    RecyclerView developer_list,publisher_list,franchise_list,theme_list,genre_list,similar_game_list;
    LinearLayoutManager manager;
    HorizontalBarChart developer_chart,publisher_chart,franchise_chart,theme_chart,genre_chart,similar_game_chart;
    boolean is_developer_shown,is_publisher_shown,is_franchise_shown,is_theme_shown,is_genre_shown,is_similar_game_shown;

    public GameDetailStatPagerFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        result = realm.where(GameDetailDatabase.class).findAll();
        count = result.size();
        rainbow = getActivity().getResources().getIntArray(R.array.score_color);
        new_rainbow = Arrays.copyOfRange(rainbow,1,rainbow.length);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pager_other_stats,container,false);
        nestedScrollView = (NestedScrollView) view.findViewById(R.id.scroll_view);
        nestedScrollView.setOnScrollChangeListener(this);
        developer_list = (RecyclerView) view.findViewById(R.id.developer_list);
        developer_list.setNestedScrollingEnabled(false);
        developer_chart = (HorizontalBarChart) view.findViewById(R.id.developer_chart);
        publisher_list = (RecyclerView) view.findViewById(R.id.publisher_list);
        publisher_list.setNestedScrollingEnabled(false);
        publisher_chart = (HorizontalBarChart) view.findViewById(R.id.publisher_chart);
        franchise_list = (RecyclerView) view.findViewById(R.id.franchise_list);
        franchise_list.setNestedScrollingEnabled(false);
        franchise_chart = (HorizontalBarChart) view.findViewById(R.id.franchise_chart);
        theme_list = (RecyclerView) view.findViewById(R.id.theme_list);
        theme_list.setNestedScrollingEnabled(false);
        theme_chart = (HorizontalBarChart) view.findViewById(R.id.theme_chart);
        genre_list = (RecyclerView) view.findViewById(R.id.genre_list);
        genre_list.setNestedScrollingEnabled(false);
        genre_chart = (HorizontalBarChart) view.findViewById(R.id.genre_chart);
        similar_game_list = (RecyclerView) view.findViewById(R.id.similar_game_list);
        similar_game_list.setNestedScrollingEnabled(false);
        similar_game_chart = (HorizontalBarChart) view.findViewById(R.id.similar_game_chart);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpViews(developer_list,developer_chart,GameDetailDatabase.DEVELOPER_TYPE,"Developer chart");
    }

    private void setUpViews(RecyclerView recyclerview,BarChart chart,int type,String desc) {
        RealmResults<GameDetailDatabase> databases = result.where().equalTo(GameDetailDatabase.TYPE,type).findAllSorted(GameDetailDatabase.COUNT, Sort.DESCENDING);
        List<BarEntry> entries = new ArrayList<>();
        int max = 0;
        int i = 0;
        for(GameDetailDatabase database : databases){
            if(max<database.getCount())
                max = database.getCount();
            entries.add(new BarEntry(i++,database.getCount()));
            if(i>=10)
                break;
        }
        setUpRecyclerView(recyclerview,databases);
        setUpBarChart(chart,entries,desc,max,databases);

     }

    private void setUpBarChart(BarChart chart, List<BarEntry> entries, String desc, int max, final RealmResults<GameDetailDatabase> databases) {

       /* BarDataSet set = new BarDataSet(entries,desc);
        set.setColors(new_rainbow);

        BarData data = new BarData(set);
        chart.setData(data);
        chart.setFitBars(true);

        Description des = chart.getDescription();
        des.setText(desc);

        chart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) Math.floor(value));

            }
        });
        chart.getAxisLeft().setLabelCount(max);
        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int val = (int) value;

                return  databases.get(val).getName();

            }
        });
        chart.setDrawValueAboveBar(false);
        chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);*/


        XAxis xl = chart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setTextColor(ContextCompat.getColor(getContext(),R.color.black_white));
        CategoryBarChartXaxisFormatter xaxisFormatter = new CategoryBarChartXaxisFormatter(databases);
        xl.setValueFormatter(xaxisFormatter);
        xl.setGranularity(1);

        YAxis yl = chart.getAxisLeft();
        yl.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yl.setDrawGridLines(false);
        yl.setEnabled(false);
        yl.setAxisMinimum(0f);


        YAxis yr = chart.getAxisRight();
        yr.setGranularity(1);
        yr.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f);



        BarDataSet set;
        set = new BarDataSet(entries, "");
        set.setColors(new_rainbow);
        set.setValueTextColor(ContextCompat.getColor(getContext(),R.color.white));
        BarData data = new BarData(set);
        data.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return Math.round(value)+"";

            }
        });
        data.setValueTextSize(10f);
        data.setBarWidth(.9f);
        chart.setData(data);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawValueAboveBar(false);
        chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);






    }

    private void setUpRecyclerView(RecyclerView recyclerview, RealmResults<GameDetailDatabase> list) {
        GameStatsAdapter adapter = new GameStatsAdapter(getContext(),list,rainbow);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerview.setAdapter(adapter);
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if(!is_publisher_shown && isViewVisible(publisher_chart)){
            is_publisher_shown = true;
            setUpViews(publisher_list,publisher_chart,GameDetailDatabase.PUBLISHER_TYPE,"Publisher chart");

        }else if(!is_theme_shown && isViewVisible(theme_chart)){
            is_theme_shown = true;
            setUpViews(theme_list,theme_chart,GameDetailDatabase.THEME_TYPE,"Theme chart");
        }else if(!is_franchise_shown && isViewVisible(franchise_chart)){
            is_franchise_shown = true;
            setUpViews(franchise_list,franchise_chart,GameDetailDatabase.FRANCHISE_TYPE,"Franchise chart");
        }else if(!is_genre_shown && isViewVisible(genre_chart)){
            is_genre_shown = true;
            setUpViews(genre_list,genre_chart,GameDetailDatabase.GENRE_TYPE,"Genre chart");
        }else if(!is_similar_game_shown && isViewVisible(similar_game_chart)){
            is_similar_game_shown = true;
            setUpViews(similar_game_list,similar_game_chart,GameDetailDatabase.SIMILAR_GAME_TYPE,"Recommended game chart");
        }


    }


    private boolean isViewVisible(View view){

        if (view!=null) {
            Rect scrollBounds = new Rect();
            nestedScrollView.getHitRect(scrollBounds);
            return view.getLocalVisibleRect(scrollBounds);
        }
        return false;
    }


    private class CategoryBarChartXaxisFormatter implements IAxisValueFormatter {

        RealmResults<GameDetailDatabase> mValues;

        CategoryBarChartXaxisFormatter(RealmResults<GameDetailDatabase> values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            int val = (int) value;
            String label = "";
            if (val >= 0 && val < mValues.size()) {
                label = mValues.get(val).getName();
            } else {
                label = "";
            }
            return label;
        }
    }




}
