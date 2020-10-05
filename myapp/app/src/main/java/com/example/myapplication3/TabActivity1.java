package com.example.myapplication3;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Bundle;
import android.view.*;
import android.widget.TabHost;
import android.app.TabActivity;
import android.widget.TabWidget;

import com.google.android.material.tabs.TabLayout;

public class TabActivity1 extends TabActivity {
    private TabHost mTab;
    public Handler handler = new Handler();

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태바 없애기

        TabHost mTab = getTabHost();
        mTab.setBackgroundColor(Color.parseColor("#FFFFFF"));
        TabWidget mWidget = getTabWidget();

        //mWidget.setBackgroundColor(Color.parseColor("#FFE604"));
        TabHost.TabSpec spec;
        LayoutInflater.from(this).inflate(R.layout.activity_tab1, mTab.getTabContentView(), true);
        spec = mTab.newTabSpec("tab1").setIndicator("상품별").setContent(new Intent(this, TotAsset_Prod.class));
        mTab.addTab(spec);
        spec = mTab.newTabSpec("tab2").setIndicator("계좌별").setContent(new Intent(this, TotAsset_Acnt.class));
        mTab.addTab(spec);


    }
}