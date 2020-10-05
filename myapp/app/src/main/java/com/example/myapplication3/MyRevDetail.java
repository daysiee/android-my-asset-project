package com.example.myapplication3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.myapplication3.ui.main.MyGlobals;
import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
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

public class MyRevDetail extends Activity {

    public static final int LOAD_SUCCESS = 101;

    private Spinner spinnerStart;
    private Spinner spinnerEnd;
    private Spinner spinnerAcnt;
    private Button btn_getRevChart, btn_goback;
    private EditText et_acntPw;

    String startDt=null;
    String endDt=null;
    String acntNum=null;
    String gnlAcNo=null;
    String gdsNo=null;
    String acntPw=null;
    String result=null;

    private String hskey;
    private String access_token = ((MainActivity)MainActivity.context_main).access_token;
    private String HpinNum = ((MainActivity)MainActivity.context_main).HpinNum;

    public static Context context_rev;
    //public ArrayList<String> dateArr = new ArrayList<String>();
    String[] dateArr = new String[1000];
    long startAsset = 0;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태바 없애기
        setContentView(R.layout.activity_my_rev_detail);

        spinnerStart = (Spinner)findViewById(R.id.spinnerDtStart); // 시작일
        spinnerEnd = (Spinner)findViewById(R.id.spinnerDtEnd); // 종료일
        spinnerAcnt = (Spinner)findViewById(R.id.spinnerAcnt); // 계좌번호
        et_acntPw = (EditText)findViewById(R.id.et_acntPw); // 계좌비밀번호
        btn_getRevChart= (Button)findViewById(R.id.btn_getRevChart);
        et_acntPw = (EditText)findViewById(R.id.et_acntPw);
        btn_goback = findViewById(R.id.btn_goback);
        //lineChart = (LineChart)findViewById(R.id.linechart);


        btn_goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyGlobals.getInstance().setisBack(true);

                Intent intent = new Intent(MyRevDetail.this, MenuActivity.class); // 현재 액티비티, 이동하고싶은 액티비티
                startActivity(intent); // 액티비티 이동
            }
        });


        // 시작날짜
        spinnerStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startDt = parent.getItemAtPosition(position).toString();
                startDt =startDt.replaceAll("-","");

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // 종료날짜
        spinnerEnd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                endDt = parent.getItemAtPosition(position).toString();
                endDt=endDt.replaceAll("-","");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 계좌번호
        spinnerAcnt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                acntNum = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 수익추이차트 조회
        btn_getRevChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acntPw = et_acntPw.getText().toString(); // 계좌 비밀번호
                String temp = acntNum.replaceAll("-","");
                gnlAcNo=temp.substring(0,9); // 종합계좌번호
                gdsNo=temp.substring(9,11); // 상품번호



                progressDialog = new ProgressDialog(MyRevDetail.this);
                progressDialog.setMessage("My수익 조회중..");
                progressDialog.show();


                // 일자별 상세수익 조회 API 호풀
                getCHART_DATA();

            }
        });

        context_rev = this;
    }

    // 계좌별 상세 수익률 조회
    public void getCHART_DATA() {

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
                    json_dataBody.put("prdCcd", "1");
                    json_dataBody.put("inqStrtDt", startDt);
                    json_dataBody.put("inqEndDt", endDt);
                    json_dataBody.put("hndlCcd","1");
                    json_dataBody.put("inqCcd","1");
                    json_dataBody.put("pwd",acntPw);
                    json_dataBody.put("gdsNo",gdsNo);
                    json_dataBody.put("gnlAcNo",gnlAcNo);

                    jsonBody.put("dataHeader", json_dataHeader);
                    jsonBody.put("dataBody", json_dataBody);

                    //System.out.println(jsonBody);
                    RequestBody body = RequestBody.create(mediaType,jsonBody.toString());
                    hskey = getHashKey(jsonBody.toString(), access_token, "UTF-8");
                    Request request = new Request.Builder()
                            .url("https://oapidev.kbsec.com:8443/v1.0/KSV/myasset/getTotAssetByDay")
                            .method("POST", body)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "Bearer "+access_token)
                            .addHeader("hsKey", hskey)
                            .build();
                    Response response = client.newCall(request).execute();
                    result = response.body().string();
                    System.out.println("API Request Success");
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                Message message = mHandler3.obtainMessage(LOAD_SUCCESS, result);
                mHandler3.sendMessage(message);
            }
        });
            thread.start();
    }

    private final MyHandler3 mHandler3 = new MyHandler3(this);

    private class MyHandler3 extends Handler{
        private final WeakReference<MyRevDetail> weakReference;

        public MyHandler3(MyRevDetail myRevDetail){
            weakReference = new WeakReference<MyRevDetail>(myRevDetail);
        }

        @Override
        public void handleMessage(Message msg){
            MyRevDetail myRevDetail = weakReference.get();

            if(myRevDetail != null){
                switch (msg.what){
                    case LOAD_SUCCESS:
                        myRevDetail.progressDialog.dismiss();

                        // 할 일

                        LineChart lineChart = (LineChart)findViewById(R.id.linechart);

                        // 라인 데이터 세팅
                        ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
                        try {
                            JSONObject jObject = new JSONObject(result);
                            String databody = jObject.getString("dataBody");
                            JSONArray jarray = new JSONObject(databody).getJSONArray("Record1");

                            // jarray.length()
                            for (int i=0; i<jarray.length(); i++){
                                JSONObject object = jarray.getJSONObject(i);
                                // 데이터 파싱

                                String plAmt = object.optString("plAmt"); // 평가손익
                                String yld = object.optString("yld"); // 손익률
                                String stdDt = object.optString("stdDt"); // 날짜
                                String trmEndAsts = object.optString("trmEndAsts"); // 종료자산
                                if(stdDt.equals(startDt)) startAsset = Long.parseLong(trmEndAsts); // 시작일 종료자산
                                double calculRate = ((Double.parseDouble(trmEndAsts))*100)/((double)startAsset);
                                calculRate = Math.round(calculRate*100)/100.0;
                                String dateStr =stdDt.substring(4,6)+'/'+stdDt.substring(6,stdDt.length());
                                dateArr[i] = dateStr;
                                plAmt = Long.toString(Long.parseLong(plAmt));
                                long Date = Long.parseLong(stdDt.substring(2,stdDt.length()));
                                // Entry cle = new Entry(Date, Float.valueOf(yld));
                                Entry cle = new Entry(i, (float)calculRate);
                                valsComp1.add(cle);
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }

                        try {

                            LineDataSet setComp = new LineDataSet(valsComp1, "누적수익률(%)");
                            setComp.setAxisDependency(YAxis.AxisDependency.LEFT);
                            setComp.setColor(R.color.colorRed);
                            setComp.setCircleColor(R.color.colorRed);
                            // use the interface ILineDataSet
                            List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                            dataSets.add(setComp);
                            LineData data = new LineData(dataSets);
                            XAxis xAxis = lineChart.getXAxis();

                            //xAxis.setLabelCount(10, true);

                            lineChart.setData(data);
                            lineChart.setScaleXEnabled(true);
                            lineChart.setScaleYEnabled(false);
                            lineChart.setScaleMinima(1.0f, 0f);
                            lineChart.getDescription().setEnabled(false);
                            lineChart.animateXY(1500, 1500); //애니메이션 기능 활성화
                            Legend legend = lineChart.getLegend();
                            legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
                            xAxis.setValueFormatter(new GraphAxisValueFormatter());
                            lineChart.invalidate(); // refresh
                            //lineChart.clear();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                        break;
                }
            }
        }
    }


    // ------------------------------------------Encript------------------------------------------------------
    //hskey 64로 전환
    public static String toBase64String(byte[] bytes){

        byte[] byteArray = org.apache.commons.codec.binary.Base64.encodeBase64(bytes);
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