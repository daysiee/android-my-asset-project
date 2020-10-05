package com.example.myapplication3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class TabActivity2 extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab3);
        TabHost tabHost = getTabHost();

        TabHost.TabSpec tabSpecSong = tabHost.newTabSpec("PRODUCT").setIndicator("상품별");
        tabSpecSong.setContent(new Intent(this, TotAsset_Prod.class));
        tabHost.addTab(tabSpecSong);

        TabHost.TabSpec tabSpecArtist = tabHost.newTabSpec("ACCOUNT").setIndicator("계좌별");
        tabSpecArtist.setContent(new Intent(this, TotAsset_Acnt.class));
        tabHost.addTab(tabSpecArtist);
        tabHost.setCurrentTab(0);


    }
}