package com.orange.orangeweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.orange.orangeweather.gson.BingYingPic;
import com.orange.orangeweather.gson.Weather;
import com.orange.orangeweather.util.HttpUtil;
import com.orange.orangeweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        //创建一个定时任务每八小时执行
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000;//8小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气
     */
    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        //如果缓存不等于空
        if (weatherString != null){
            //解析缓存的数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            //从缓存中得到天气的id
            String weatherId = weather.basic.weatherId;
            //请求数据
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=4b7efcf812684ae59fdefdfa0e87130c";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)){
                        //请求成功就更新数据, 把数据保存到SP中
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                    }
                }
            });

        }
    }

    /**
     * 更新每日一图
     */
    private void updateBingPic() {
        String requestBingPic = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                BingYingPic bingYingPic = Utility.handleBingYingPicResponse(responseText);
                final String bingBasePicUrl = "http://cn.bing.com" + bingYingPic.images.get(0).bingBasePicUrl;
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic", bingBasePicUrl);
                editor.apply();
            }
        });
    }

}
