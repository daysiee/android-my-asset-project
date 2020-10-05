package com.example.myapplication3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class TotAsset_Prod extends Activity {

    public static final int LOAD_SUCCESS = 101;
    DecimalFormat formatter = new DecimalFormat("###,###"); // Lont to String
    private String hskey;
    private String access_token = ((MainActivity)MainActivity.context_main).access_token;
    private String HpinNum = ((MainActivity)MainActivity.context_main).HpinNum;
    String result = null;
    double demo;

    String TotAssetVal, DepositVal = null;
    TextView tv_totAssetVal, tv_deposit;
    LinearLayout mChartLayout;
    TableLayout mTableLayout;
    private ProgressDialog progressDialog;
    public PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태바 없애기
        setContentView(R.layout.activity_tot_asset__prod);
        PieChart pieChart = findViewById(R.id.piechart);
        tv_totAssetVal = findViewById(R.id.tv_totAssetVal);
        //tv_deposit = findViewById(R.id.tv_deposit);
        mTableLayout = new TableLayout(this);
        mChartLayout = (LinearLayout)findViewById(R.id.chart_layout);

        progressDialog = new ProgressDialog(TotAsset_Prod.this);
        progressDialog.setMessage("My자산 조회중..");
        progressDialog.show();

        // 상품별 자산조회 API 호출
        get_TOTASSET_PROD();

    }

    // 표 데이터 생성
    public void displayChartTable() {
        mTableLayout.setLayoutParams(new TableLayout.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        mTableLayout.setStretchAllColumns(true);

        try {
            JSONObject jObject = new JSONObject(result);
            String databody = jObject.getString("dataBody");
            JSONArray jarray = new JSONObject(databody).getJSONArray("Record1");

            // jarray.length()
            for (int i=0; i<jarray.length(); i++){
                JSONObject object = jarray.getJSONObject(i);
                // 데이터 파싱
                String scrtsKndNm = object.optString("scrtsKndNm"); // 상품명
                String valAmt = object.optString("valAmt"); // 잔액
                String valSgrvtP2 = object.optString("valSgrvtP2"); // 비중
                String valPl = object.optString("valPl"); ; // 평가손익
                String valYldP2 = object.optString("valYldP2");  // 손익률
                // 비중 String  실수형변환
                float d_valSgrvtP2 = Float.valueOf(valSgrvtP2);
                if(Long.parseLong(valAmt) == 0 || scrtsKndNm.equals("총자산") || scrtsKndNm.equals("예수금")) continue;
                // 잔액이 0인 상품, 총자산, 예수금 제외
                valAmt = Long.toString(Long.parseLong(valAmt));
                valPl = Long.toString(Long.parseLong(valPl));
                valYldP2 = Float.toString(Float.valueOf(valYldP2));
                TableRow row = new TableRow(this);
                row.setLayoutParams((new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT)));

                // 상품명 세팅
                TextView valueTV = new TextView(this);
                double calculRate = ((Double.parseDouble(valAmt))*100)/demo;
                calculRate = Math.round(calculRate*100)/100.0; // 총자산-예수금 대비 상품자산액 비중 계산
                valueTV.setText(scrtsKndNm+" "+Double.toString(calculRate)+"%");
                valueTV.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                //valueTV.setGravity(Gravity.CENTER);
                valueTV.setTextSize(18);
                valueTV.setPadding(60,100,15,100);
                valueTV.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.table_outside));
                row.addView(valueTV);
                // 데이터 세팅
                TextView valueTV2 = new TextView(this);
                valueTV2.setText( formatter.format(Long.parseLong(valAmt))+"원"); // 상품별 자산액 표시
                valueTV2.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                valueTV2.setGravity(Gravity.END);
                valueTV2.setTextSize(18);
                valueTV2.setPadding(20,100,60,100);
                valueTV2.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.table_outside));
                row.addView(valueTV2);

                mTableLayout.addView(row);
            }
            mChartLayout.addView(mTableLayout);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    // 파싱 및 차트데이터 생성
    public void make_CHART_DATA(List <PieEntry> entries){
        try {

            JSONObject jObject = new JSONObject(result);
            String databody = jObject.getString("dataBody");
            JSONArray jarray = new JSONObject(databody).getJSONArray("Record1");

            for (int i=0; i<jarray.length();i++){
                JSONObject object = jarray.getJSONObject(i);
                String scrtsKndNm = object.optString("scrtsKndNm"); // 상품명
                String valAmt = object.optString("valAmt"); // 잔액
                if(scrtsKndNm.equals("총자산"))
                    TotAssetVal = Long.toString(Long.parseLong(valAmt));
                else if(scrtsKndNm.equals("예수금"))
                    DepositVal = Long.toString(Long.parseLong(valAmt));
                else continue;
            } // 비중 계산을 위해 먼저 필요

            for (int i=0; i<jarray.length(); i++){
                JSONObject object = jarray.getJSONObject(i);
                // 데이터 파싱
                String scrtsKndNm = object.optString("scrtsKndNm"); // 상품명
                String valAmt = object.optString("valAmt"); // 잔액
                String valPl = object.optString("valPl"); ; // 평가손익
                String valYldP2 = object.optString("valYldP2");  // 손익률

                if(scrtsKndNm.equals("총자산") || scrtsKndNm.equals("예수금")) continue;
                else{
                    long tempLong = Long.parseLong(TotAssetVal)-Long.parseLong(DepositVal);
                    String tempString = Long.toString(tempLong);
                    demo = Double.parseDouble(tempString);
                    double calculRate = ((Double.parseDouble(valAmt))*100)/demo;
                    calculRate = Math.round(calculRate*100)/100.0; // 총자산-예수금 대비 상품자산액 비중 계산
                    if(calculRate >0.0)
                        entries.add(new PieEntry((float)calculRate, scrtsKndNm)); // (상품명, 비중)
                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    // 상품별 자산조회
    public void get_TOTASSET_PROD() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .readTimeout(30,TimeUnit.SECONDS)
                            .writeTimeout(30,TimeUnit.SECONDS)
                            .build();
                    MediaType mediaType = MediaType.parse("application/json");
                    JSONObject jsonBody = new JSONObject();
                    JSONObject json_dataHeader = new JSONObject();
                    JSONObject json_dataBody = new JSONObject();

                    // dataHeader 세팅
                    json_dataHeader.put("carrier" ,"KT");
                    json_dataHeader.put("appVersion" , "..");
                    json_dataHeader.put("deviceOs", "Android");
                    json_dataHeader.put("appName" , "..");
                    json_dataHeader.put("subChannel" ,"subChannel");
                    json_dataHeader.put("deviceModel" ,"Android");
                    json_dataHeader.put("udId" , "UDID");
                    json_dataHeader.put("connectionType" , "..");

                    // databody 세팅
                    json_dataBody.put("inqClsf", "3");
                    json_dataBody.put("scrtsCcd", "9");
                    json_dataBody.put("rqStdGb", "3");
                    json_dataBody.put("ssn", HpinNum);

                    jsonBody.put("dataHeader", json_dataHeader);
                    jsonBody.put("dataBody", json_dataBody);

                    //System.out.println(jsonBody);
                    RequestBody body = RequestBody.create(mediaType,jsonBody.toString());
                    hskey = getHashKey(jsonBody.toString(), access_token, "UTF-8");
                    Request request = new Request.Builder()
                            .url("https://oapidev.kbsec.com:8443/v1.0/KSV/myasset/getAssetByProd")
                            .method("POST", body)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "Bearer "+access_token)
                            .addHeader("hsKey", hskey)
                            .build();
                    Response response = client.newCall(request).execute();
                    result = response.body().string();

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                Message message = mHandler.obtainMessage(LOAD_SUCCESS, result);
                mHandler.sendMessage(message);
            }
        });

            thread.start();
    }



    private final MyHandler mHandler = new MyHandler(this);


    class MyHandler extends Handler {
        private final WeakReference<TotAsset_Prod> weakReference;

        public MyHandler(TotAsset_Prod totAsset_prod) {
            weakReference = new WeakReference<TotAsset_Prod>(totAsset_prod);
        }

        @Override
        public void handleMessage(Message msg) {

            TotAsset_Prod totAsset_prod = weakReference.get();

            if (totAsset_prod != null) {
                switch (msg.what) {

                    case LOAD_SUCCESS:
                        totAsset_prod.progressDialog.dismiss();

                        //String jsonString = (String)msg.obj;

                        //할 일

                        // 차트 엔트리 생성
                        List<PieEntry> entries = new ArrayList<>();
                        make_CHART_DATA(entries);

                        // 총자산, 예수금 데이터 생성
                        TotAssetVal = formatter.format(Long.parseLong(TotAssetVal));
                        DepositVal = formatter.format(Long.parseLong(DepositVal));
                        tv_totAssetVal.setText(TotAssetVal);
                        //tv_deposit.setText(DepositVal);

                        // 차트 생성
                        PieDataSet set = new PieDataSet(entries, ""); // 차트 라벨은 여기서 세팅
                        set.setColors(ColorTemplate.MATERIAL_COLORS);

                        set.setValueTextColor(Color.BLACK);
                        set.setValueLineColor(Color.TRANSPARENT); // 라인색 지정
                        set.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                        PieChart pieChart = findViewById(R.id.piechart);
                        pieChart.setEntryLabelColor(Color.GRAY); //라벨색 지정
                        PieData data = new PieData(set);
                        pieChart.setData(data);
                        pieChart.getDescription().setEnabled(false);
                        pieChart.animateXY(1500, 1500); //애니메이션 기능 활성화
                        pieChart.invalidate(); // refresh

                        // 표 생성
                        displayChartTable();


                        break;
                }
            }
        }
    }


// ------------------------------------------Encript------------------------------------------------------
    //hskey 64로 전환
    public static String toBase64String(byte[] bytes){

        byte[] byteArray = Base64.encodeBase64(bytes);
        return new String(byteArray);

    }
    //hskey 획득
    public String getHashKey(String jsonStr, String key, String charset){
        String hash = "";
        try{
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretkey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            sha256HMAC.init(secretkey);

            //hash = Base64.encodeBase64String(sha256HMAC.doFinal(jsonStr.getBytes(charset)));
            hash = toBase64String(sha256HMAC.doFinal(jsonStr.getBytes(charset)));

        }catch (Exception e) {
        }
        return  hash;
    }
}