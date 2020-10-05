package com.example.myapplication3;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.WildcardType;
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

public class TotAsset_Acnt extends Activity {

    public static final int LOAD_SUCCESS = 101;
    DecimalFormat formatter = new DecimalFormat("###,###"); // Lont to String
    //DecimalFormat formatter = new DecimalFormat("###-###"); // Lont to String
    private String hskey;
    private String access_token = ((MainActivity)MainActivity.context_main).access_token;
    private String HpinNum = ((MainActivity)MainActivity.context_main).HpinNum;
    String result = null;

    private ProgressDialog progressDialog;
    public PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태바 없애기
        setContentView(R.layout.activity_tot_asset__acnt);

        // 리사이클러뷰 등록
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        progressDialog = new ProgressDialog(TotAsset_Acnt.this);
        progressDialog.setMessage("My자산 조회중..");
        progressDialog.show();

        // 계좌별 자산조회 API 호출
        get_TOTASSET_ACNT();

    }

    // 파싱 및 카드데이터 생성
    public void make_CARD_DATA(List <CardItem> dataList){
        try {

            JSONObject jObject = new JSONObject(result);
            String databody = jObject.getString("dataBody");
            JSONArray jarray = new JSONObject(databody).getJSONArray("Record1");

            // jarray.length()
            for (int i=0; i<jarray.length(); i++){
                JSONObject object = jarray.getJSONObject(i);
                String gdsTypDtlsNm = object.optString("gdsTypDtlsNm"); // 계좌명
                String valAmt = object.optString("valAmt"); // 잔액
                String valPl = object.optString("valPl"); ; // 평가손익
                String valYldP2 = object.optString("valYldP2");  // 손익률
                String acc_no = object.optString("acc_no"); // 계좌명
                String prd_no = object.optString("prd_no"); // 계좌명
                if(Long.parseLong(valAmt) == 0) continue;
                // 잔액이 0인 상품, 총자산, 예수금 제외

                //String account = formatter.format(Long.parseLong(acc_no))+prd_no;
                String account = acc_no+prd_no;
                String acntNum = account.substring(0,3)+'-'+account.substring(3,6)+'-'+account.substring(6,9)+'-'+account.substring(9,account.length());
                valAmt =  formatter.format(Long.parseLong(valAmt));

                valPl = Long.toString(Long.parseLong(valPl));


                if(valPl.charAt(0)!='0') {
                    if (valPl.charAt(0) == '-'){

                        String temp = valPl.substring(1,valPl.length());
                        valPl = '-'+formatter.format(Long.parseLong(temp));
                    }
                    else{
                        valPl = formatter.format(Long.parseLong(valPl));
                    }
                }

                valYldP2 = Float.toString(Float.valueOf(valYldP2));
                dataList.add(new CardItem(gdsTypDtlsNm, acntNum                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 , valAmt+"원", valPl+"원 ("+valYldP2+"%)" ));
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    // 계좌별 자산조회
    public void get_TOTASSET_ACNT() {

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
                    json_dataBody.put("rqStdGb", "4");
                    json_dataBody.put("ssn", HpinNum);

                    jsonBody.put("dataHeader", json_dataHeader);
                    jsonBody.put("dataBody", json_dataBody);

                    //System.out.println(jsonBody);
                    RequestBody body = RequestBody.create(mediaType,jsonBody.toString());
                    hskey = getHashKey(jsonBody.toString(), access_token, "UTF-8");
                    Request request = new Request.Builder()
                            .url("https://oapidev.kbsec.com:8443/v1.0/KSV/myasset/getAssetByAccount")
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
                Message message = mHandler2.obtainMessage(LOAD_SUCCESS, result);
                mHandler2.sendMessage(message);
            }
        });

            thread.start();

    }

    private final MyHandler2 mHandler2 = new MyHandler2(this);

    private class MyHandler2 extends Handler{
        private final WeakReference <TotAsset_Acnt> weakReference;

        public MyHandler2(TotAsset_Acnt totAsset_acnt){
            weakReference = new WeakReference<TotAsset_Acnt>(totAsset_acnt);
        }

        @Override
        public void handleMessage(Message msg){
            TotAsset_Acnt totAsset_acnt = weakReference.get();

            if(totAsset_acnt != null){
                switch (msg.what){

                    case LOAD_SUCCESS:
                        totAsset_acnt.progressDialog.dismiss();

                        // 리사이클러뷰 등록
                        RecyclerView recyclerView = findViewById(R.id.recycler_view);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TotAsset_Acnt.this);
                        recyclerView.setLayoutManager(layoutManager);
                        // 카드리스트 생성
                        List<CardItem> dataList = new ArrayList<>();
                        make_CARD_DATA(dataList);

                        MyRecyclerAdapter adapter = new MyRecyclerAdapter(dataList);
                        recyclerView.setAdapter(adapter);


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