package com.example.myapplication3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.style.StyleSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class Activity1 extends Activity {

    private Button btn_TotAsset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

        btn_TotAsset = findViewById(R.id.btn_TotAsset);
        btn_TotAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //str = et_id.getText().toString(); // EditText로부터 입력값을 받아옴
                Intent intent = new Intent(Activity1.this, TabActivity2.class); // 현재 액티비티, 이동하고싶은 액티비티
                //intent.putExtra("str", str); // 서브액티비에서 쓸 이름, 실제 데이터 // 서브액티비티로 데이터 쏴 줌!
                // 즉, put액티비티를 통해 "str"에 값을 담아서 intent를 보냄
                startActivity(intent); // 액티비티 이동
            }
        });
    }
}