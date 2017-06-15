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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.randomlocks.gamesnote.Adapter.GameStatsAdapter;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.RealmDatabase.GameListDatabase;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.grabner.circleprogress.CircleProgressView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by randomlock on 6/5/2017.
 */

public class GameStatPagerFragment extends Fragment implements NestedScrollView.OnScrollChangeListener {

    NestedScrollView nestedScrollView;
    CircleProgressView game_count;
    PieChart status_pie_chart;
    BarChart score_bar_chart;
    HorizontalBarChart medium_bar_chart;
    RecyclerView score_list_view,new_list_view,updated_list_view;
    GameStatsAdapter top_score_adapter,new_game_adapter,recent_game_adapter;
    Realm realm;
    RealmResults<GameListDatabase> result;
    int count;
    private int[] rainbow;
    private int[] new_rainbow;
    private int text_color;

    private String[] status;
    boolean isAnimated = false;
    boolean isMediumAnimated = false;


    public GameStatPagerFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        result = realm.where(GameListDatabase.class).findAll();
        count = result.size();
        rainbow = getActivity().getResources().getIntArray(R.array.score_color);
        new_rainbow = Arrays.copyOfRange(rainbow,1,rainbow.length);
        status = getActivity().getResources().getStringArray(R.array.status);
        text_color = ContextCompat.getColor(getContext(),R.color.black_white);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_pager_game_stats,container,false);
        nestedScrollView = (NestedScrollView) view.findViewById(R.id.scroll_view);
        nestedScrollView.setOnScrollChangeListener(this);
        game_count = (CircleProgressView) view.findViewById(R.id.score_game_count);
        game_count.setValueAnimated(count);
        game_count.setBarColor(rainbow[count <= 100 ? count/10 : 10 ]);
        status_pie_chart = (PieChart) view.findViewById(R.id.status_pie_chart);
        score_list_view = (RecyclerView) view.findViewById(R.id.score_list);
        score_list_view.setNestedScrollingEnabled(false);
        score_bar_chart = (BarChart) view.findViewById(R.id.score_bar_chart);
        new_list_view = (RecyclerView) view.findViewById(R.id.new_game_list);
        new_list_view.setNestedScrollingEnabled(false);
        updated_list_view = (RecyclerView) view.findViewById(R.id.updated_game_list);
        updated_list_view.setNestedScrollingEnabled(false);
        medium_bar_chart = (HorizontalBarChart) view.findViewById(R.id.medium_bar_chart);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpStatusPieChart();
        setUpTopScoreList();

    }



    private void setUpTopScoreList() {

        RealmResults<GameListDatabase> databases =  result.where().findAllSorted("score", Sort.DESCENDING);
        setUpRecyclerView(score_list_view,databases,true,true);
    }

    private void setUpScoreBarChart() {
        int max = 0;
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i <=100 ; i=i+10) {
            int value = (int) result.where().equalTo("score",i).count();
            if(value>max)
                max = value;
            entries.add(new BarEntry(i,value));
        }

        BarDataSet set = new BarDataSet(entries,"score distribution");
        set.setColors(new_rainbow);
        set.setValueTextColor(text_color);


        BarData data = new BarData(set);
        data.setBarWidth(5);
        data.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

                if(value==0)
                    return "";

                return Math.round(value)+"";

            }
        });

        score_bar_chart.setData(data);
        score_bar_chart.setFitBars(true);
        score_bar_chart.getXAxis().setTextColor(text_color);
        score_bar_chart.getAxisLeft().setTextColor(text_color);
        score_bar_chart.getAxisRight().setTextColor(text_color);
        score_bar_chart.getLegend().setTextColor(text_color);
        score_bar_chart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) Math.floor(value));

            }
        });
        score_bar_chart.getAxisLeft().setLabelCount(max);



        Description des = score_bar_chart.getDescription();
        des.setText("");
        score_bar_chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

    }

    private void setUpMediumBarChart() {

      /*  List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(10,realm.where(GameListDatabase.class).equalTo("medium","Physical").count()));
        entries.add(new BarEntry(15,realm.where(GameListDatabase.class).equalTo("medium","Digital").count()));
        BarDataSet set = new BarDataSet(entries,"medium distribution");
        set.setColors(new_rainbow);
        BarData data = new BarData(set);
        medium_bar_chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int val = (int) value;
                String empty = "";
                if(val==10)
                    return "Physical";
                else if (val==15)
                    return "Digital";

                else return empty;
            }
        });
        medium_bar_chart.getAxisRight().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) Math.floor(value));
            }
        });
        medium_bar_chart.getAxisRight().setLabelCount(3);
        medium_bar_chart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) Math.floor(value));
            }
        });

        medium_bar_chart.getAxisLeft().setLabelCount(3);

        medium_bar_chart.setDrawBarShadow(false);
        medium_bar_chart.setDrawValueAboveBar(true);
        medium_bar_chart.getDescription().setEnabled(false);
        medium_bar_chart.setPinchZoom(false);
        medium_bar_chart.setDrawGridBackground(false);
        medium_bar_chart.setData(data);
        medium_bar_chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);*/

        ArrayList<String> labels = new ArrayList<>();
        labels.add("Physical");
        labels.add("Digital");

        XAxis xl = medium_bar_chart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setTextColor(text_color);
        CategoryBarChartXaxisFormatter xaxisFormatter = new CategoryBarChartXaxisFormatter(labels);
        xl.setValueFormatter(xaxisFormatter);
        xl.setGranularity(1);

        YAxis yl = medium_bar_chart.getAxisLeft();
        yl.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yl.setDrawGridLines(false);
        yl.setEnabled(false);
        yl.setAxisMinimum(0f);

        YAxis yr = medium_bar_chart.getAxisRight();
        yr.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return  String.valueOf((int) Math.floor(value));
            }
        });
        yr.setGranularity(1);
        yr.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f);

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        String medium[] = getActivity().getResources().getStringArray(R.array.medium);
        for (int i = 0; i < 2; i++) {
            int val = (int) realm.where(GameListDatabase.class).equalTo("medium",medium[i+1]).count();
            yVals1.add(new BarEntry(i,val));
        }

        BarDataSet set1;
        set1 = new BarDataSet(yVals1, "");
        set1.setColors(new_rainbow);
        set1.setValueTextColor(ContextCompat.getColor(getContext(),R.color.white));
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);
        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(.9f);
        data.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return Math.round(value)+"";

            }
        });
        medium_bar_chart.setData(data);
        medium_bar_chart.getLegend().setEnabled(false);
        medium_bar_chart.getDescription().setEnabled(false);
        medium_bar_chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);











    }


    private void setUpStatusPieChart() {

        List<PieEntry> entries = new ArrayList<>();

        for (int i = 0; i <status.length ; i++) {
            float status_count =  result.where().equalTo("status",i+1).count();
            if (status_count>0) {
                entries.add(new PieEntry(status_count,status[i]));
            }
        }

        if (!entries.isEmpty()) {
            PieDataSet set = new PieDataSet(entries,"");
            set.setColors(new_rainbow);
            set.setSliceSpace(0.6f);
            PieData data = new PieData(set);
            status_pie_chart.setData(data);
            Description des = status_pie_chart.getDescription();
            des.setEnabled(false);
            status_pie_chart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
            status_pie_chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            status_pie_chart.getLegend().setTextColor(text_color);
            status_pie_chart.getLegend().setYEntrySpace(5);
            data.setValueTextColor(ContextCompat.getColor(getContext(),R.color.white));
            data.setValueTextSize(10);
            data.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return "" + ((int) value);
                }
            });
            status_pie_chart.setCenterText("Status stats");
            status_pie_chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        }

    }




    private void setUpRecentGameList() {
      RealmResults<GameListDatabase>  databases =   result.where().findAllSorted("date_added",Sort.DESCENDING);
      setUpRecyclerView(new_list_view,databases,false,false);

    }

    private void setUpNewGameList() {
        RealmResults<GameListDatabase>  databases =   result.where().findAllSorted("last_updated",Sort.DESCENDING);
        setUpRecyclerView(updated_list_view,databases,false,true);
    }

    void setUpRecyclerView(RecyclerView recycler_view,List<GameListDatabase> list,boolean isScoreViewShown,boolean isNewGame){
        GameStatsAdapter adapter = new GameStatsAdapter(getContext(),list,rainbow,isScoreViewShown,isNewGame);
        recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_view.setAdapter(adapter);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(realm!=null && !realm.isClosed()){
            realm.close();
            realm = null;
        }

    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if(!isAnimated && isViewVisible(score_bar_chart)){
            isAnimated = true;
            setUpScoreBarChart();
            setUpNewGameList();

        }

        if(!isMediumAnimated && isViewVisible(medium_bar_chart)){
            isMediumAnimated = true;
            setUpMediumBarChart();
            setUpRecentGameList();
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

        ArrayList<String> mValues;

        CategoryBarChartXaxisFormatter(ArrayList<String> values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            int val = (int) value;
            String label = "";
            if (val >= 0 && val < mValues.size()) {
                label = mValues.get(val);
            } else {
                label = "";
            }
            return label;
        }
    }


}
