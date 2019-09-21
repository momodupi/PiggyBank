package com.momodupi.piggybank;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class ChartActivity extends AppCompatActivity {

    private ViewPager tabViewPager;
    private TabLayout chartTabLayout;

    private RecyclerView recyclerView;
    private ChartAdapter chartAdapter;
    private Robot robot;

    //private List<Fragment> tabFragments;
    //private String charttype = "month";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        //TabLayout chartTabLayout;
        //RecyclerView.LayoutManager layoutManager;



        chartAdapter = new ChartAdapter(this);
        //layoutManager = new LinearLayoutManager(this);

        robot = new Robot(this, DatabaseHelper.BOOKNAME);

        chartTabLayout = findViewById(R.id.charttab);
        tabViewPager = findViewById(R.id.tabframe);

        TabFragmentAdapter tabFragmentAdapter = new TabFragmentAdapter(this, getSupportFragmentManager());
        tabViewPager.setAdapter(tabFragmentAdapter);

        chartTabLayout.setupWithViewPager(tabViewPager);

        Toolbar toolbar = findViewById(R.id.toolbar_chart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(ChartActivity.this, MainActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            ChartActivity.this.finish();

            overridePendingTransition(R.anim.leftin, R.anim.rightout);
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChartActivity.this, MainActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        ChartActivity.this.finish();

        overridePendingTransition(R.anim.leftin, R.anim.rightout);
    }

}
