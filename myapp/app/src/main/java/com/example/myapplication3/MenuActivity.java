package com.example.myapplication3;

        import androidx.appcompat.app.AppCompatActivity;

        import android.graphics.Color;
        import android.os.Bundle;
        import android.app.TabActivity;
        import android.content.Intent;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.WindowManager;
        import android.widget.Button;
        import android.widget.TabHost;
        import android.widget.TabHost.TabSpec;

        import com.example.myapplication3.ui.main.MyGlobals;

public class MenuActivity extends TabActivity {
    /** Called when the activity is first created. */
    Button Tab1,Tab2;
    TabHost tabHost;
    public static String Tab="";
    public String access_token;
    public String HpinNum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태바 없애기

        setContentView(R.layout.activity_menu);
        tabHost = getTabHost();
        boolean isMenu2 = MyGlobals.getInstance().getisBack();

        Button Tab0;
        Tab1=(Button)findViewById(R.id.Tab1);
        Tab2=(Button)findViewById(R.id.Tab2);

        // Tab for tab1
        TabHost.TabSpec spec1 = tabHost.newTabSpec("Tab1");
        // setting Title and Icon for the Tab
        spec1.setIndicator("Tab1");
        Intent Intent1 = new Intent(this, Activity1.class);
        spec1.setContent(Intent1);

        /*
        Tab1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Tab1.setBackgroundColor(Color.LTGRAY);

                } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Tab1.setBackgroundColor(Color.TRANSPARENT);
                }
                return false;
            }
        });*/

        // Tab for tab2
        TabHost.TabSpec spec2 = tabHost.newTabSpec("Tab2");
        // setting Title and Icon for the Tab
        spec2.setIndicator("Tab2");
        Intent Intent2 = new Intent(this, Activity2.class);
        spec2.setContent(Intent2);
        /*
        Tab2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Tab2.setBackgroundColor(Color.LTGRAY);

                } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Tab2.setBackgroundColor(Color.TRANSPARENT);
                }
                return false;
            }
        });*/

        // Adding all TabSpec to TabHost
        tabHost.addTab(spec1); // Adding tab1
        tabHost.addTab(spec2); // Adding tab2

        if(isMenu2){
            Tab2.performClick();
            MyGlobals.getInstance().setisBack(false);
        }


    }


    //======================= click Handling for the tab layout buttons=============
    public void tabHandler(View target) {
        if (target.getId() == R.id.Tab1) {
            Tab="this is Tab 1";
            tabHost.setCurrentTab(0);

        } else if (target.getId() == R.id.Tab2) {
            Tab="this is Tab 2";

            tabHost.setCurrentTab(1);

        }
    }
}
