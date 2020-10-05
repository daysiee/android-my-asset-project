package com.example.myapplication3;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends Activity {

    public String saml_res;
    public String access_token;
    public String HpinNum;
    private EditText et_id;
    private EditText et_pw;
    private Button btn_login;
    private String hskey;
    private String id;
    private String pw;
    public static Context context_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태바 없애기
        setContentView(R.layout.activity_main);

        btn_login = findViewById(R.id.btn_login);
        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = et_id.getText().toString(); // EditText로부터 입력값을 받아옴
                pw = et_pw.getText().toString(); // EditText로부터 입력값을 받아옴
                get_SAML();
                get_ACCESSTOKEN();
                get_HPIN();
                Intent intent = new Intent(MainActivity.this, MenuActivity.class); // 현재 액티비티, 이동하고싶은 액티비티
                startActivity(intent); // 액티비티 이동
            }
        });
        context_main = this;
    }

    // ------------------------------------------Login 작업 ------------------------------------------------------
    // saml assertion 생성
    public void get_SAML() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String result = null;

                try {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("application/json");
                    RequestBody body = RequestBody.create(mediaType, "{\n\t\"dataHeader\" : {\n\t\t\"udId\" : \"UDID\",\n\t\t\"subChannel\" : \"subChannel\",\n\t\t\"deviceModel\" : \"Android\",\n\t\t\"deviceOs\" : \"Android\",\n\t\t\"carrier\" : \"KT\",\n\t\t\"connectionType\" : \"..\",\n\t\t\"appName\" : \"..\",\n\t\t\"appVersion\" : \"..\",\n\t\t\"hsKey\" : \"body\",\n\t\t\"scrNo\" : \"0000\"\n\t},\n\t\"dataBody\" : {\n\t\t\"type\" : \"4\",\n\t\t\"ciNo\" : \"yOV2MgaR+OoKU8mSzZn4LTiV8nYEKgHTFOmYI+z120IhHBzXuZmOglpa6YnPfpRjSDvhE4Uk9xAdpkisqrS7ig==\",\n\t\t\"clientId\" : \"l7xx6c19b49d527d467b97c1c4dab5fdf0e3\",\n\t\t\"loginType\" : \"2\"\n\t}\n}");
                    Request request = new Request.Builder()
                            .url("https://oapidev.kbsec.com:8443/v1.0/OAuth/saml/assertion")
                            .method("POST", body)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Cookie", "WMONID=RntUzVTI98e; JSESSIONID=VM8wpVnABrlwSPlTPo6vNOfVAAx6oja4caLKZpYNTkvOUqusV0eRbZlTME6x7qMU.amV1c19kb21haW4va2JvcGVuYXBp")
                            .build();
                    Response response = client.newCall(request).execute();

                    result = response.body().string();

                    // samlAssertion값 파싱 후 저장
                    JSONObject jObject = new JSONObject(result);
                    String temp = jObject.getString("dataBody");
                    jObject = new JSONObject(temp);
                    saml_res = jObject.getString("samlAssertion");
                    //System.out.println(result);

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // access 토큰 발급
    public void get_ACCESSTOKEN() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String result = null;

                try {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("application/json");

                    JSONObject jsonBody = new JSONObject();
                    JSONObject json_dataHeader = new JSONObject();
                    JSONObject json_dataBody = new JSONObject();

                    // dataHeader 세팅
                    json_dataHeader.put("udId","UDID");
                    json_dataHeader.put("subChannel" ,"subChannel");
                    json_dataHeader.put("deviceModel" ,"Android");
                    json_dataHeader.put("deviceOs", "Android");
                    json_dataHeader.put("carrier" ,"KT");
                    json_dataHeader.put("connectionType" , "..");
                    json_dataHeader.put("appName" , "KBSEC_APP");
                    json_dataHeader.put("appVersion" , "..");
                    json_dataHeader.put("scrNo" , "0000");

                    // databody 세팅
                    json_dataBody.put("samlAssertion", ""+saml_res+"="); //saml 입력
                    json_dataBody.put("clientId" , "l7xx6c19b49d527d467b97c1c4dab5fdf0e3");
                    json_dataBody.put("clientSecret" , "363eecba4a4d41b5a29fc45628430610");
                    json_dataBody.put("grantType" ,"urn:ietf:params:oauth:grant-type:saml2-bearer");
                    json_dataBody.put("scope" ,"public security");

                    jsonBody.put("dataHeader", json_dataHeader);
                    jsonBody.put("dataBody", json_dataBody);
                    //System.out.println(jsonBody);
                    RequestBody body = RequestBody.create(mediaType,jsonBody.toString());
                    Request request = new Request.Builder()
                            .url("https://oapidev.kbsec.com:8443/v1.0/OAuth/token/access")
                            .method("POST", body)
                            .addHeader("Content-Type", "application/json")
                            .build();
                    Response response = client.newCall(request).execute();

                    result = response.body().string();

                    // accesstoken값 파싱 후 저장
                    JSONObject jObject = new JSONObject(result);
                    String temp = jObject.getString("dataBody");
                    jObject = new JSONObject(temp);
                    access_token = jObject.getString("access_token");

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // H-pin번호 획득
    public void get_HPIN() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String result = null;
                try {

                    OkHttpClient client = new OkHttpClient().newBuilder()
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
                    json_dataBody.put("vrsn", "1");//saml 입력
                    json_dataBody.put("untyLgnId", id);
                    json_dataBody.put("mtrlRqsClsf", "1");
                    json_dataBody.put("cnnctScrtNo", pw);

                    jsonBody.put("dataHeader", json_dataHeader);
                    jsonBody.put("dataBody", json_dataBody);

                    //System.out.println(jsonBody);
                    RequestBody body = RequestBody.create(mediaType,jsonBody.toString());

                    hskey = getHashKey(jsonBody.toString(), access_token, "UTF-8");
                    //System.out.println(hskey);

                    Request request = new Request.Builder()
                            .url("https://oapidev.kbsec.com:8443/v1.0/KSV/myasset/getPin")
                            .method("POST", body)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "Bearer "+access_token)
                            .addHeader("hsKey", hskey)
                            .build();
                    Response response = client.newCall(request).execute();

                    result = response.body().string();

                    // Hpin값 파싱 후 저장
                    JSONObject jObject = new JSONObject(result);
                    String temp = jObject.getString("dataBody");
                    jObject = new JSONObject(temp);
                    HpinNum = jObject.getString("hdPinNo");
                    System.out.println(HpinNum);

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
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