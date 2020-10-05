package com.example.myapplication3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.myapplication3.ui.main.MyGlobals;

public class MyGoalSetting extends AppCompatActivity {

    Button okBtn, cancleBtn;
    String res = null;
    Button goalBtn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_goal_setting);

        
        okBtn = (Button) findViewById(R.id.okBtn);
        cancleBtn = (Button) findViewById(R.id.cancleBtn);

    }


    float newGoal;
    public void mGoal1(View v){

        float num = (float) 1.5;
        float now = MyGlobals.getInstance().getNow();
        newGoal = num * now;
    }
    public void mGoal2(View v){

        float num = (float) 2.0;
        float now = MyGlobals.getInstance().getNow();
        newGoal = num * now;
    }
    public void mGoal3(View v){
        float num = (float) 2.5;
        float now = MyGlobals.getInstance().getNow();
        newGoal = num * now;
    }


    public void mOk(View v){
        res ="확인";
        MyGlobals.getInstance().setGoal(newGoal); // 새로운 Goal 세팅
        MyGlobals.getInstance().setisNew(); // isNew True 세팅
        Intent intent = new Intent(MyGoalSetting.this, MyAssetStep.class);
        startActivity(intent); // 액티비티 이동
    }
    public void mCancle(View v){
        res="취소";
        System.out.println(res);
        finish();
    }

    @Override
    public  boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
    @Override
    public void onBackPressed(){
        return;
    }
}



