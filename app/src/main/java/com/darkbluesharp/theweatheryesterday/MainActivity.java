package com.darkbluesharp.theweatheryesterday;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.net.ParseException;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.darkbluesharp.theweatheryesterday.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    TextView text;
    ImageView refresh ;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.text);
        refresh = (ImageView) findViewById(R.id.refresh);


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
            String weather = jsonObject.getString("weather");

            JSONObject weatherjsonObject = new JSONObject(weather);
            String summary = weatherjsonObject.getString("summary");
            JSONArray summaryjsonArray = new JSONArray(summary);

            Log.e("weather","summary====="+summary);
            for (int i=0; i < summaryjsonArray.length(); i++) {
                if(i==0){
                    JSONObject summaryjsonObject = summaryjsonArray.getJSONObject(i);
                    //어제날씨
                    String yesterday = summaryjsonObject.getString("yesterday");
                    Log.e("weather","yesterday====="+yesterday);

                    JSONObject yesterdayjsonObject = new JSONObject(yesterday);
                    String temperatureYesterday = yesterdayjsonObject.getString("temperature");
                    Log.e("weather","temperatureYesterday====="+temperatureYesterday);

                    JSONObject yesterdaytemperaturejsonObject = new JSONObject(temperatureYesterday);
                    String tmaxYesterday = yesterdaytemperaturejsonObject.getString("tmax");
                    String tminYesterday = yesterdaytemperaturejsonObject.getString("tmin");
                    Log.e("weather","tmaxYesterday====="+tmaxYesterday);
                    Log.e("weather","tminYesterday====="+tminYesterday);


                    //오늘날씨
                    String today = summaryjsonObject.getString("today");
                    Log.e("weather","today====="+today);

                    JSONObject todayjsonObject = new JSONObject(today);
                    String temperatureToday = todayjsonObject.getString("temperature");
                    Log.e("weather","temperatureToday====="+temperatureToday);

                    JSONObject todaytemperaturejsonObject = new JSONObject(temperatureToday);
                    String tmaxToday = todaytemperaturejsonObject.getString("tmax");
                    String tminToday = todaytemperaturejsonObject.getString("tmin");
                    Log.e("weather","tmaxToday====="+tmaxToday);
                    Log.e("weather","tminToday====="+tminToday);


                    //내일날씨
                    String tomorrow = summaryjsonObject.getString("tomorrow");
                    Log.e("weather","tomorrow====="+tomorrow);

                    JSONObject tomorrowjsonObject = new JSONObject(tomorrow);
                    String temperatureTomorrow = tomorrowjsonObject.getString("temperature");
                    Log.e("weather","temperatureTomorrow====="+temperatureTomorrow);

                    JSONObject tomorrowtemperaturejsonObject = new JSONObject(temperatureTomorrow);
                    String tmaxTomorrow = tomorrowtemperaturejsonObject.getString("tmax");
                    String tminTomorrow = tomorrowtemperaturejsonObject.getString("tmin");
                    Log.e("weather","tmaxTomorrow====="+tmaxTomorrow);
                    Log.e("weather","tminTomorrow====="+tminTomorrow);

                    //내일모래날씨
                    String dayAfterTomorrow = summaryjsonObject.getString("dayAfterTomorrow");
                    Log.e("weather","dayAfterTomorrow====="+dayAfterTomorrow);

                    JSONObject dayAfterTomorrowjsonObject = new JSONObject(dayAfterTomorrow);
                    String temperatureDayAfterTomorrow = dayAfterTomorrowjsonObject.getString("temperature");
                    Log.e("weather","temperatureDayAfterTomorrow====="+temperatureDayAfterTomorrow);

                    JSONObject dayAfterTomorrowtemperaturejsonObject = new JSONObject(temperatureDayAfterTomorrow);
                    String tmaxDayAfterTomorrow = dayAfterTomorrowtemperaturejsonObject.getString("tmax");
                    String tminDayAfterTomorrow = dayAfterTomorrowtemperaturejsonObject.getString("tmin");
                    Log.e("weather","tmaxDayAfterTomorrow====="+tmaxDayAfterTomorrow);
                    Log.e("weather","tminDayAfterTomorrow====="+tminDayAfterTomorrow);

                    //위치
                    String grid = summaryjsonObject.getString("grid");
                    Log.e("weather","grid====="+grid);

                    JSONObject gridjsonObject = new JSONObject(grid);
                    String city = gridjsonObject.getString("city");
                    String county = gridjsonObject.getString("county");
                    Log.e("weather","city====="+city);
                    Log.e("weather","county====="+county);




                    text.setText(
                                "어제  "+
                            "  "+substringcomma(tminYesterday)+" º  ~"+
                            "  "+substringcomma(tmaxYesterday)+" º\n\n"+

                                "오늘  "+
                            "  "+substringcomma(tminToday)+" º  ~"+
                            "  "+substringcomma(tmaxToday)+" º\n\n"+

                                "내일  "+
                            "  "+substringcomma(tminTomorrow)+" º  ~"+
                            "  "+substringcomma(tmaxTomorrow)+" º\n\n"+

                                "모래  "+
                             "  "+substringcomma(tminDayAfterTomorrow)+" º  ~"+
                            "  "+substringcomma(tmaxDayAfterTomorrow)+" º\n\n\n\n\n\n"+
                                        city+"   "+county

                    );
                }
            }



          /*  JSONObject jsonObject = new JSONObject(jsonInfo);
            String predictions = jsonObject.getString("predictions");
            JSONArray jsonArray = new JSONArray(predictions);
            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject subJsonObject = jsonArray.getJSONObject(i);
                String description = subJsonObject.getString("description");
                String id = subJsonObject.getString("id");

                String structured_formatting = subJsonObject.getString("structured_formatting");
                JSONObject subJsonObject2 = new JSONObject(structured_formatting);
                String main_text = subJsonObject2.getString("main_text");

                System.out.println("description: " + description + "\n" +
                        "id: " + id + "\n" +
                        "main_text: " + main_text);*/


        } catch (JSONException e) {
            e.printStackTrace();
        }

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
             appKey = "l7xx26f65cec2522414e8ce89887fcf52947";
             lat= Double.toString(latitude);
             lon= Double.toString(longitude);
        }

        new Thread() {
            public void run() {
                String naverHtml = httpConnection("https://apis.openapi.sk.com/weather/summary?appKey="+appKey+"&version=2&lat="+lat+"&lon="+lon);

                Bundle bun = new Bundle();
                bun.putString("HTML_DATA", naverHtml);

                Message msg = handler.obtainMessage();
                msg.setData(bun);
                handler.sendMessage(msg);
            }
        }.start();
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
