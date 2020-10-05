package com.example.myapplication3;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.content.ContextCompat;

    import android.app.Activity;
    import android.content.Context;
    import android.content.Intent;
    import android.graphics.Color;
    import android.net.Uri;
    import android.os.Bundle;
    import android.view.View;
    import android.view.WindowManager;
    import android.widget.Button;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.example.myapplication3.ui.main.MyGlobals;
    import com.github.mikephil.charting.charts.HorizontalBarChart;
    import com.github.mikephil.charting.components.LimitLine;
    import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
    import com.github.mikephil.charting.data.CombinedData;
    import com.github.mikephil.charting.data.Entry;
    import com.github.mikephil.charting.data.LineData;
    import com.github.mikephil.charting.highlight.Highlight;
    import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
    import com.skydoves.balloon.ArrowOrientation;
    import com.skydoves.balloon.Balloon;

    import org.w3c.dom.Text;

    import java.text.DecimalFormat;
    import java.util.ArrayList;
import java.util.List;


public class MyAssetStep extends Activity {
    public float srcGoal=150000.f, srcNow=100000f, srcOrigin=50000.f;
    DecimalFormat formatter = new DecimalFormat("###,###"); // Lont to String
    HorizontalBarChart barChart;
    TextView tv_revePink, tv_reveSky, tv_nowAsset, tv_rate;
    Button btn_call, btn_join, btn_setting, btn_goback;

    ImageView iv_star1, iv_star2, iv_star3;
    public static Context context_step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태바 없애기
        setContentView(R.layout.activity_my_asset_step);

        tv_revePink = findViewById(R.id.tv_revePink);
        tv_revePink.setVisibility(TextView.INVISIBLE);
        tv_reveSky = findViewById(R.id.tv_reveSky);
        tv_reveSky.setVisibility(TextView.INVISIBLE);

        tv_nowAsset = findViewById(R.id.tv_nowAsset);
        tv_rate = findViewById(R.id.tv_rate);
        btn_call = findViewById(R.id.btn_call);
        btn_join = findViewById(R.id.btn_join);
        btn_setting = findViewById(R.id.btn_setting);
        btn_goback = findViewById(R.id.btn_goback);
        iv_star1 = findViewById(R.id.iv_star1);
        iv_star2 = findViewById(R.id.iv_star2);
        iv_star3 = findViewById(R.id.iv_star3);



        float goal = MyGlobals.getInstance().getGoal();
        final float now = MyGlobals.getInstance().getNow();
        final float origin = MyGlobals.getInstance().getOrigin();

        //final float now = 100000.f;
        //final float origin = 50000.f;
        tv_nowAsset.setText(formatter.format(Long.parseLong(Integer.toString((int)goal))));
        if(MyGlobals.getInstance().getisNew()){
            iv_star1.setVisibility(View.INVISIBLE);
            iv_star2.setVisibility(View.INVISIBLE);
            iv_star3.setVisibility(View.INVISIBLE);
            tv_nowAsset.setText(formatter.format(Long.parseLong(Integer.toString((int)MyGlobals.getInstance().getGoal()))));
        }

        int goal_rate;
        goal_rate = (int)((now * 100)/goal);
        tv_rate.setText(Integer.toString(goal_rate)+"%");

        barChart = findViewById(R.id.barchart);
        List<BarEntry> entries = new ArrayList<BarEntry>();
        List<BarEntry> entries2 = new ArrayList<BarEntry>();
        BarDataSet barDataSet;
        BarDataSet barDataSet2;
        BarData barData;
        if(now > origin){

            entries.add(new BarEntry(0f, now));
            entries2.add(new BarEntry(0f, origin));
            barDataSet = new BarDataSet(entries, "now"); // 빨간색 먼저
            barDataSet2 = new BarDataSet(entries2,"Origin"); // 그다음이 회색
            barData = new BarData(barDataSet);
            barData.addDataSet(barDataSet2);
            barData.setBarWidth(0.2f);

            barDataSet.setColor(Color.parseColor("#E91E63")); // 평가금 컬러 세팅
            barDataSet2.setColor(Color.parseColor("#BDBDBD")); // 매입금 컬러 세팅

            LimitLine ll1 = new LimitLine(origin, "매입금\n" + Integer.toString((int)origin));
            LimitLine ll2 = new LimitLine(now, "평가금\n"+Integer.toString((int)now));
            LimitLine ll3 = new LimitLine(goal, "목표액\n"+Integer.toString((int)goal));
            ll1.setLineWidth(2f);  ll2.setLineWidth(2f);  ll3.setLineWidth(2f);
            //ll1.enableDashedLine(10f, 1f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
            ll1.setLineColor(barChart.getContext().getResources().getColor(R.color.colorGray));
            ll2.setLineColor(barChart.getContext().getResources().getColor(R.color.colorRed));

            ll3.setLineColor(barChart.getContext().getResources().getColor(R.color.colorGoal));

            ll2.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
            ll3.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
            ll1.setTextSize(10f); ll2.setTextSize(10f); ll3.setTextSize(10f);
            barChart.getAxisRight().addLimitLine(ll1);
            barChart.getAxisRight().addLimitLine(ll2);
            barChart.getAxisRight().addLimitLine(ll3);
        }
        else if ( now == origin){


            entries.add(new BarEntry(0f, now));
            barDataSet = new BarDataSet(entries, "now"); // 회색 먼저
            barData = new BarData(barDataSet);

            barData.setBarWidth(0.2f);
            barDataSet.setColor(Color.parseColor("#BDBDBD")); // 평가금 컬러 세팅

            barDataSet.setColor(Color.parseColor("#BDBDBD")); // 평가금 컬러 세팅
            LimitLine ll2 = new LimitLine(now, "평가금(=매입금)\n"+Integer.toString((int)now));
            LimitLine ll3 = new LimitLine(goal, "목표액\n"+Integer.toString((int)goal));
            ll2.setLineWidth(2f);  ll3.setLineWidth(2f);
            ll2.setLineColor(barChart.getContext().getResources().getColor(R.color.colorGray));
            ll3.setLineColor(barChart.getContext().getResources().getColor(R.color.colorGoal));

            ll2.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
            ll3.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
            ll2.setTextSize(10f); ll3.setTextSize(10f);
            barChart.getAxisRight().addLimitLine(ll2);
            barChart.getAxisRight().addLimitLine(ll3);
        }
        else{
            entries.add(new BarEntry(0f, origin)); // 매입금 먼저
            entries2.add(new BarEntry(0f, now));
            barDataSet = new BarDataSet(entries, "Origin"); // 회색 먼저
            barDataSet2 = new BarDataSet(entries2,"now"); // 파란색 나중에
            barData = new BarData(barDataSet);
            barData.addDataSet(barDataSet2);
            barData.setBarWidth(0.2f);

            barDataSet.setColor(Color.parseColor("#BDBDBD")); // 평가금 컬러 세팅
            barDataSet2.setColor(Color.parseColor("#197DCC")); // 매입금 컬러 세팅

            LimitLine ll1 = new LimitLine(origin, "매입금\n" + Integer.toString((int)origin));
            LimitLine ll2 = new LimitLine(now, "평가금\n"+Integer.toString((int)now));
            LimitLine ll3 = new LimitLine(goal, "목표액\n"+Integer.toString((int)goal));
            ll1.setLineWidth(2f);  ll2.setLineWidth(2f);  ll3.setLineWidth(2f);
            //ll1.enableDashedLine(10f, 1f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
            ll1.setLineColor(barChart.getContext().getResources().getColor(R.color.colorGray));
            ll2.setLineColor(barChart.getContext().getResources().getColor(R.color.colorBlue));
            ll3.setLineColor(barChart.getContext().getResources().getColor(R.color.colorGoal));

            ll2.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
            ll3.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
            ll1.setTextSize(10f); ll2.setTextSize(10f); ll3.setTextSize(10f);
            barChart.getAxisRight().addLimitLine(ll1);
            barChart.getAxisRight().addLimitLine(ll2);
            barChart.getAxisRight().addLimitLine(ll3);

        }


        barChart.getXAxis().setDrawLabels(false);
        barChart.getAxisLeft().setDrawLabels(false); // 상단
        barChart.getAxisRight().setDrawLabels(false); // 하단
        barChart.getAxisRight().setAxisMaximum(goal);
        barChart.getAxisRight().setAxisMinimum(0.f);
        barChart.getAxisLeft().setAxisMaximum(goal);
        barChart.getAxisLeft() .setAxisMinimum(0.f);
        barChart.getXAxis().setDrawGridLines(false);
        //barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);

        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.setScaleYEnabled(false);
        barChart.setScaleXEnabled(false);
        //barChart.setAutoScaleMinMaxEnabled(true);

        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.animateXY(1000, 1000);
        barChart.invalidate();

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener()
        {
            @Override
            public void onValueSelected(Entry e, Highlight h)
            {
                float x=e.getX();
                float y=e.getY();
                System.out.println("터치");
                if(now>origin){
                    int amt = (int)now - (int)origin;
                    String amtStr="\r\n   +" + Integer.toString(amt);
                    tv_revePink.setText(amtStr);
                    tv_revePink.setVisibility(TextView.VISIBLE);
                }
                else if(origin>now){
                    int amt = (int)origin - (int)now;
                    String amtStr="\r\n   -" + Integer.toString(amt);
                    tv_reveSky.setText(amtStr);
                    tv_reveSky.setVisibility(TextView.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected()
            {
                if(now>origin){
                    tv_revePink.setVisibility(TextView.INVISIBLE);
                }
                else if(origin>now){
                    tv_reveSky.setVisibility(TextView.INVISIBLE);
                }
            }
        });

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("상담원 연결하기");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:1588-6611")); // 현재 액티비티, 이동하고싶은 액티비티
                startActivity(intent); // 액티비티 이동
            }
        });
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("프라임클럽 가입하기");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://app.kbsec.com/scr/index.html?id=0801")); // 현재 액티비티, 이동하고싶은 액티비티
                startActivity(intent); // 액티비티 이동
            }
        });

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("My Goal 설정");
                // 다이얼로그

                switch(view.getId()){
                    case R.id.btn_setting:
                        Intent intent = new Intent(MyAssetStep.this, MyGoalSetting.class); // 현재 액티비티, 이동하고싶은 액티비티
                        startActivityForResult(intent, 1);

                        break;
                }
            }
        });

        btn_goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyGlobals.getInstance().setisBack(true);
                boolean now = MyGlobals.getInstance().getisBack();
                System.out.println(now);
                Intent intent = new Intent(MyAssetStep.this, MenuActivity.class); // 현재 액티비티, 이동하고싶은 액티비티
                startActivity(intent); // 액티비티 이동
            }
        });

        context_step = this;
    }
}