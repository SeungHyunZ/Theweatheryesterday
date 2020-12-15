package com.darkbluesharp.theweatheryesterday;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


public class MainActivity extends AppCompatActivity {

    TextView text;
    ImageView refresh ;
    TextView timeRelease;
    TextView city_contry;

    TextView max1, min1, max2, min2, max3, min3, max4, min4 ;
    LinearLayout up1, mid1, down1, up2, mid2, down2, up3, mid3, down3, up4, mid4, down4;

    ImageView sky1, sky2,sky3,sky4;

    private AdView mAdView;

    public LocationManager locationManager;
    private static final int REQUEST_CODE_LOCATION = 2;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};



    private String appKey = "l7xx26f65cec2522414e8ce89887fcf52947";
    private String lat="37.595451";
    private String lon="127.0261907";

    private String  tmaxYesterday;
    private String  tminYesterday;
    private String  tmaxToday;
    private String  tminToday;
    private String  tmaxTomorrow;
    private String  tminTomorrow;
    private String  tmaxDayAfterTomorrow;
    private String  tminDayAfterTomorrow;

    private Long timezone_offset;
    private Long tsLong;

    private Long yesterdaytime1 ; //어제
    private Long yesterdaytime2 ; //그제

    private ArrayList<String> temp = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //add bof
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        //add eof


        text = (TextView) findViewById(R.id.text);
        refresh = (ImageView) findViewById(R.id.refresh);
        timeRelease = (TextView) findViewById(R.id.timeRelease);
        city_contry = (TextView) findViewById(R.id.city_contry);

        max1 = (TextView) findViewById(R.id.max1);
        //sky1 = (ImageView) findViewById(R.id.sky1);
        min1 = (TextView) findViewById(R.id.min1);

        max2 = (TextView) findViewById(R.id.max2);
        sky2 = (ImageView) findViewById(R.id.sky2);
        min2 = (TextView) findViewById(R.id.min2);

        max3 = (TextView) findViewById(R.id.max3);
        sky3 = (ImageView) findViewById(R.id.sky3);
        min3 = (TextView) findViewById(R.id.min3);

        max4 = (TextView) findViewById(R.id.max4);
        sky4 = (ImageView) findViewById(R.id.sky4);
        min4 = (TextView) findViewById(R.id.min4);

        up1 = (LinearLayout) findViewById(R.id.up1);
        mid1 = (LinearLayout) findViewById(R.id.mid1);
        down1 = (LinearLayout) findViewById(R.id.down1);

        up2 = (LinearLayout) findViewById(R.id.up2);
        mid2 = (LinearLayout) findViewById(R.id.mid2);
        down2 = (LinearLayout) findViewById(R.id.down2);

        up3 = (LinearLayout) findViewById(R.id.up3);
        mid3 = (LinearLayout) findViewById(R.id.mid3);
        down3 = (LinearLayout) findViewById(R.id.down3);

        up4 = (LinearLayout) findViewById(R.id.up4);
        mid4 = (LinearLayout) findViewById(R.id.mid4);
        down4 = (LinearLayout) findViewById(R.id.down4);


        ImageView refresh = (ImageView) findViewById(R.id.refresh) ;
        refresh.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkLocationServicesStatus()) {
                    showDialogForLocationServiceSetting();
                }else {

                    checkRunTimePermission();
                }
            }
        });

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }

      //  출처: https://mainia.tistory.com/5090 [녹두장군 - 상상을 현실로]
        /*api 출처는 sk플레닛 : https://openapi.sk.com/myproject/apiConsole?pjtSeq=1000021706&ordSeq=27200&gdsSeq=264#none*/
    //    String jsonstring = httpConnection("https://apis.openapi.sk.com/weather/summary?appKey=l7xx26f65cec2522414e8ce89887fcf52947&version=2&lat=37.595451&lon=127.0261907");
     //   text.setText(jsonstring);
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
            String jsonInfo = bun.getString("HTML_DATA");
           // text.setText(jsonInfo);

            weatersetting(jsonInfo);
        }
    };

    Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
            String jsonInfo = bun.getString("HTML_DATA2");
            // text.setText(jsonInfo);

            startTomorrow(jsonInfo);
        }
    };

    Handler handler3 = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
            String jsonInfo = bun.getString("HTML_DATA3");
            // text.setText(jsonInfo);

            startTomorrow2(jsonInfo);
        }
    };

    /*출처: https://cofs.tistory.com/335 [CofS]*/
    public String httpConnection(String targetUrl) {
        URL url = null;
        HttpURLConnection conn = null;
        String jsonData = "";
        BufferedReader br = null;
        StringBuffer sb = null;
        String returnText = "";

        try {
            url = new URL(targetUrl);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("GET");
            conn.connect();

            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            sb = new StringBuffer();

            while ((jsonData = br.readLine()) != null) {
                sb.append(jsonData);
            }

            returnText = sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return returnText;
    }

    public String substringcomma(String t){

        int idx = t.indexOf(".");
        String substringcomma = t.substring(0, idx+2);

        return  substringcomma;
    }

    public void weatersetting(String jsonInfo){
        try {
            JSONObject jsonObject = new JSONObject(jsonInfo);

            //현재시간
            String currentString = jsonObject.getString("current");
            Log.e("weather","currentString====="+currentString);
            JSONObject dtjson= new JSONObject(currentString);
            String dtString = dtjson.getString("dt");

            // 현재시간을 msec 으로 구한다.
            long now = System.currentTimeMillis();
            // 현재시간을 date 변수에 저장한다.
            Date date = new Date(now);
            // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            // nowDate 변수에 값을 저장한다.
            String formatDate = sdfNow.format(date);



            timeRelease.setText(formatDate); //업데이트 날짜 세팅

            //도시
            String timezoneString = jsonObject.getString("timezone");
            city_contry.setText(timezoneString);//도시 세팅

            //타임존 차이 구하기
            timezone_offset =  Long.parseLong(jsonObject.getString("timezone_offset"));

            //오늘, 내일, 모래 날씨
            String daily = jsonObject.getString("daily");
            Log.e("daily","currentString====="+daily);
            JSONArray dailyjsonArray= new JSONArray(daily);

            for (int i=0; i < dailyjsonArray.length(); i++) {
                if(i==0){
                    JSONObject dailyjson1 = dailyjsonArray.getJSONObject(i);
                    String temp = dailyjson1.getString("temp");
                    JSONObject tempjson = new JSONObject(temp);
                    tminToday = tempjson.getString("min");
                    tmaxToday = tempjson.getString("max");

                    max2.setText(substringcomma(tmaxToday)+" º");
                    min2.setText(substringcomma(tminToday)+" º");

                    String weather = dailyjson1.getString("weather");
                    Log.e("weather","currentString====="+weather);
                    JSONArray weatherjsonArray= new JSONArray(weather);
                    JSONObject weatherjson = weatherjsonArray.getJSONObject(0);
                    String skyNameToday = weatherjson.getString("description");
                    String icon = weatherjson.getString("icon");
                  //  sky2.setText(skyNameToday);

                    // Glide로 이미지 표시하기
                    String imageUrl = "https://openweathermap.org/img/wn/"+icon+"@2x.png";
                    Glide.with(this).load(imageUrl).into(sky2);



                }
                if(i==1){
                    JSONObject dailyjson1 = dailyjsonArray.getJSONObject(i);
                    String temp = dailyjson1.getString("temp");
                    JSONObject tempjson = new JSONObject(temp);
                    tminTomorrow = tempjson.getString("min");
                    tmaxTomorrow = tempjson.getString("max");

                    max3.setText(substringcomma(tmaxTomorrow)+" º");
                    min3.setText(substringcomma(tminTomorrow)+" º");

                    String weather = dailyjson1.getString("weather");
                    Log.e("weather","currentString====="+weather);
                    JSONArray weatherjsonArray= new JSONArray(weather);
                    JSONObject weatherjson = weatherjsonArray.getJSONObject(0);
                    String skyNameTomorrow = weatherjson.getString("description");
                    String icon = weatherjson.getString("icon");
                    //  sky2.setText(skyNameToday);

                    // Glide로 이미지 표시하기
                    String imageUrl = "https://openweathermap.org/img/wn/"+icon+"@2x.png";
                    Glide.with(this).load(imageUrl).into(sky3);
                }
                if(i==2){
                    JSONObject dailyjson1 = dailyjsonArray.getJSONObject(i);
                    String temp = dailyjson1.getString("temp");
                    JSONObject tempjson = new JSONObject(temp);
                    tminDayAfterTomorrow = tempjson.getString("min");
                    tmaxDayAfterTomorrow = tempjson.getString("max");

                    max4.setText(substringcomma(tmaxDayAfterTomorrow)+" º");
                    min4.setText(substringcomma(tminDayAfterTomorrow)+" º");

                    String weather = dailyjson1.getString("weather");
                    Log.e("weather","currentString====="+weather);
                    JSONArray weatherjsonArray= new JSONArray(weather);
                    JSONObject weatherjson = weatherjsonArray.getJSONObject(0);
                    String skyNameDayAfterTomorrow = weatherjson.getString("description");
                    String icon = weatherjson.getString("icon");
                    //  sky2.setText(skyNameToday);

                    // Glide로 이미지 표시하기
                    String imageUrl = "https://openweathermap.org/img/wn/"+icon+"@2x.png";
                    Glide.with(this).load(imageUrl).into(sky4);
                }
            }


            new Thread() {
                public void run() {

                    SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("YYYY-MM-dd HH:mm:ssZ");

                    tsLong = System.currentTimeMillis()/1000;  //현재시간
                    String ts = tsLong.toString();
                    Log.e("timezone_offset",timezone_offset+"");
                    Long gmttime = tsLong - timezone_offset ;  //gmt 시간
Log.e("weather","gmttime="+dateFormat2.format(gmttime*1000)+" gmttime= "+dateFormat2.format(tsLong*1000));
                   if(dateFormat.format(gmttime*1000).equals(dateFormat.format(tsLong*1000))){
Log.e("weather", "어제, 그제");
                       yesterdaytime1 = tsLong - 86400; //어제
                       yesterdaytime2 = tsLong - 86400*2; //그제
                    }else{
                       Log.e("weather", "오늘, 어제");
                       yesterdaytime1 = tsLong ; //어제
                       yesterdaytime2 = tsLong - 86400; //그제
                    }

                    //String naverHtml = httpConnection("https://api.openweathermap.org/data/2.5/onecall?lat="+lat+"&lon="+lon+"&exclude=alerts&appid=e9e3ed325461bd506285bdb140285195&lang=kr&units=metric");
                    String naverHtml = httpConnection("https://api.openweathermap.org/data/2.5/onecall/timemachine?lat="+lat+"&lon="+lon+"&exclude=alerts&appid=e9e3ed325461bd506285bdb140285195&lang=kr&units=metric&dt="+yesterdaytime2);

                    Bundle bun = new Bundle();
                    bun.putString("HTML_DATA2", naverHtml);

                    Message msg = handler2.obtainMessage();
                    msg.setData(bun);
                    handler2.sendMessage(msg);
                }
            }.start();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void  setgraph(){
        float maxf1 = Float.parseFloat(tmaxYesterday);
        float minf1 = Float.parseFloat(tminYesterday);
        float maxf2 = Float.parseFloat(tmaxToday);
        float minf2 = Float.parseFloat(tminToday);
        float maxf3 = Float.parseFloat(tmaxTomorrow);
        float minf3 = Float.parseFloat(tminTomorrow);
        float maxf4 = Float.parseFloat(tmaxDayAfterTomorrow);
        float minf4 = Float.parseFloat(tminDayAfterTomorrow);

        float maxf ; //최대온도
        float minf ; //최소온도


        //최대온도 구하기
        if(maxf1>=maxf2){ maxf = maxf1;
        }else{ maxf = maxf2; }
        if(maxf>=maxf3){
        }else{ maxf=maxf3; }
        if(maxf>=maxf4){
        }else{ maxf=maxf4; }
        Log.e("weather","maxf====="+maxf);  //날씨
        //최소온도 구하기
        if(minf1<=minf2){ minf = minf1;
        }else{ minf = minf2; }
        if(minf<=minf3){
        }else{ minf=minf3; }
        if(minf<=minf4){
        }else{ minf=minf4; }
        Log.e("weather","minf====="+minf);  //날씨

        float lange = maxf - minf;
        Log.e("weather","lange====="+lange);  //날씨


        LinearLayout.LayoutParams paramsup1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        LinearLayout.LayoutParams paramsmid1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        LinearLayout.LayoutParams paramsdown1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);

        paramsup1.weight = Math.round(maxf-maxf1);
        up1.setLayoutParams(paramsup1);
        paramsmid1.weight = Math.round(maxf1-minf1);
        mid1.setLayoutParams(paramsmid1);
        paramsdown1.weight = Math.round(minf1-minf);
        down1.setLayoutParams(paramsdown1);
        Log.e("weather","Math.round(maxf-maxf1)====="+Math.round(maxf-maxf1));  //날씨
        Log.e("weather","Math.round(maxf1-minf1)====="+Math.round(maxf1-minf1));  //날씨
        Log.e("weather","Math.round(minf1-minf)====="+Math.round(minf1-minf));  //날씨

        LinearLayout.LayoutParams paramsup2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        LinearLayout.LayoutParams paramsmid2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        LinearLayout.LayoutParams paramsdown2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);

        paramsup2.weight = Math.round(maxf-maxf2);
        up2.setLayoutParams(paramsup2);
        paramsmid2.weight = Math.round(maxf2-minf2);
        mid2.setLayoutParams(paramsmid2);
        paramsdown2.weight = Math.round(minf2-minf);
        down2.setLayoutParams(paramsdown2);

        LinearLayout.LayoutParams paramsup3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        LinearLayout.LayoutParams paramsmid3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        LinearLayout.LayoutParams paramsdown3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);

        paramsup3.weight = Math.round(maxf-maxf3);
        up3.setLayoutParams(paramsup3);
        paramsmid3.weight = Math.round(maxf3-minf3);
        mid3.setLayoutParams(paramsmid3);
        paramsdown3.weight = Math.round(minf3-minf);
        down3.setLayoutParams(paramsdown3);

        LinearLayout.LayoutParams paramsup4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        LinearLayout.LayoutParams paramsmid4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        LinearLayout.LayoutParams paramsdown4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);

        paramsup4.weight = Math.round(maxf-maxf4);
        up4.setLayoutParams(paramsup4);
        paramsmid4.weight = Math.round(maxf4-minf4);
        mid4.setLayoutParams(paramsmid4);
        paramsdown4.weight = Math.round(minf4-minf);
        down4.setLayoutParams(paramsdown4);

        Toast.makeText(MainActivity.this, "날씨 업데이트가 완료되었습니다.", Toast.LENGTH_LONG).show();
    }

    public void startGPSweather(){
        //위치 값을 가져올 수 있음
        //사용자의 위치 수신을 위한 세팅
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //사용자의 현재 위치

        Location userLocation = getMyLocation();
        if( userLocation != null ) {
            double latitude = userLocation.getLatitude();
            double longitude = userLocation.getLongitude();
            //   userVO.setLat(latitude);
            //   userVO.setLon(longitude);
            System.out.println("////////////현재 내 위치값 : "+latitude+","+longitude);
             //appKey = "l7xx26f65cec2522414e8ce89887fcf52947";
            appKey = "e9e3ed325461bd506285bdb140285195";
            lat= Double.toString(latitude);
            lon= Double.toString(longitude);
        }
        Toast.makeText(MainActivity.this, "날씨를 업데이트 합니다.", Toast.LENGTH_LONG).show();
        new Thread() {
            public void run() {
                //String naverHtml = httpConnection("https://apis.openapi.sk.com/weather/summary?appKey="+appKey+"&version=2&lat="+lat+"&lon="+lon);
                String naverHtml = httpConnection("https://api.openweathermap.org/data/2.5/onecall?lat="+lat+"&lon="+lon+"&exclude=alerts&appid=e9e3ed325461bd506285bdb140285195&lang=kr&units=metric");

                Bundle bun = new Bundle();
                bun.putString("HTML_DATA", naverHtml);

                Message msg = handler.obtainMessage();
                msg.setData(bun);
                handler.sendMessage(msg);
            }
        }.start();
    }

    public void startTomorrow(String jsonInfo){

        try {
            Log.e("hourly", "currentString=====" + jsonInfo);
            JSONObject jsonObject = new JSONObject(jsonInfo);
            String hourly = jsonObject.getString("hourly");
            Log.e("hourly", "currentString=====" + hourly);
            JSONArray hourlyjsonArray = new JSONArray(hourly);

            for (int i = 15; i <24; i++) {
                JSONObject hourlyjson = hourlyjsonArray.getJSONObject(i);
                temp.add(hourlyjson.getString("temp"));
                Log.e("yesterdaytemp",hourlyjson.getString("dt")+" "+hourlyjson.getString("temp"));
            }


            new Thread() {
                public void run() {
                    String naverHtml = httpConnection("https://api.openweathermap.org/data/2.5/onecall/timemachine?lat="+lat+"&lon="+lon+"&exclude=alerts&appid=e9e3ed325461bd506285bdb140285195&lang=kr&units=metric&dt="+yesterdaytime1);

                    Bundle bun = new Bundle();
                    bun.putString("HTML_DATA3", naverHtml);

                    Message msg = handler3.obtainMessage();
                    msg.setData(bun);
                    handler3.sendMessage(msg);
                }
            }.start();



        }catch(Exception e){
            Log.e("Exception1",e+"");
        }

    }

    public void startTomorrow2(String jsonInfo){

        try {
            Log.e("hourly", "currentString=====" + jsonInfo);
            JSONObject jsonObject = new JSONObject(jsonInfo);
            String hourly = jsonObject.getString("hourly");
            Log.e("hourly", "currentString=====" + hourly);
            JSONArray hourlyjsonArray = new JSONArray(hourly);

            for (int i = 0; i <16; i++) {
                JSONObject hourlyjson = hourlyjsonArray.getJSONObject(i);
                temp.add(hourlyjson.getString("temp"));
                Log.e("yesterdaytemp",hourlyjson.getString("dt")+" "+hourlyjson.getString("temp"));
            }

            ArrayList<Float> templist = new ArrayList<Float>();
            for(int i = 0; i <temp.size(); i++){
                templist.add(Float.parseFloat(temp.get(i)));
            }

            tmaxYesterday = Collections.max(templist).toString();
            tminYesterday = Collections.min(templist).toString();

            max1.setText(substringcomma(tmaxYesterday)+" º");
            min1.setText(substringcomma(tminYesterday)+" º");


            setgraph();
        }catch(Exception e){
            Log.e("Exception2",e+"");
        }

    }


    /**
     * 사용자의 위치를 수신
     */
    private Location getMyLocation() {
      //  locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location currentLocation = null;
        // Register the listener with the Location Manager to receive location updates
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("////////////사용자에게 권한을 요청해야함");
         //   requestPermissions( new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, this.REQUEST_CODE_LOCATION);
          //  getMyLocation(); //이건 써도되고 안써도 되지만, 전 권한 승인하면 즉시 위치값 받아오려고 썼습니다!
        }
        else {
            System.out.println("////////////권한요청 안해도됨");

            // 수동으로 위치 구하기
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            if (currentLocation != null) {
                double lng = currentLocation.getLongitude();
                double lat = currentLocation.getLatitude();
            }else{
                locationProvider = LocationManager.NETWORK_PROVIDER;
                currentLocation = locationManager.getLastKnownLocation(locationProvider);
            }
        }
        return currentLocation;
    }



    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 서비스를 활성화해주세요");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음

            startGPSweather();

        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

               startGPSweather();
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

}
