package com.orange.orangeweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.orange.orangeweather.gson.BingYingPic;
import com.orange.orangeweather.gson.Forecast;
import com.orange.orangeweather.gson.Weather;
import com.orange.orangeweather.service.AutoUpdateService;
import com.orange.orangeweather.util.HttpUtil;
import com.orange.orangeweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;

    public Button navButton;

    public SwipeRefreshLayout swipeRefresh;

    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView bingPicImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置界面全景显示
        if (Build.VERSION.SDK_INT >= 21){                           //判断系统版本是否5.0以上
            View decorView = getWindow().getDecorView();            //拿到当前活动的DecorView
            decorView.setSystemUiVisibility(                        //调用decorView的方法来改变系统UI显示
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN           //活动的布局会显示在状态栏上面
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);       //将状态栏设置成透明色
        }
        setContentView(R.layout.activity_weather);

        //初始化各种控件
        bingPicImg = findViewById(R.id.bing_pic_img);

        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degerr_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);

        swipeRefresh = findViewById(R.id.swipe_refresh);            //实例化下拉刷新
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary); //和颜色

        drawerLayout = findViewById(R.id.drawer_layout);            //实例化DrawerLayout
        navButton = findViewById(R.id.nav_button);                  //和菜单按钮
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);       //点击弹出碎片
            }
        });

        //先尝试从本地缓存中读取天气数据
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);

        //定义一个查询天气的id
        final String weatherId;

        if (weatherString != null){
            //有缓存时直接解析天气数据, 设置数据显示
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;//有缓存时从缓存得到天气id
            showWeatherInfo(weather);
        } else {
            //无缓存时去服务器查询天气
            weatherId = getIntent().getStringExtra("weather_id");//没缓存时从Intent得到传过来的天气id
            weatherLayout.setVisibility(View.INVISIBLE);//没有数据时要把ScrollView隐藏
            requestWeather(weatherId);//请求数据
        }

        //下拉刷新
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

        //尝试从SP中读取缓存的背景图片的路径
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null){
            //有图片路径, 就直接用Glide库设置
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            //没有图片路径, 就调用loadBingPic()方法加载
//            loadBingPic();
            loadBingYingPic();
        }

    }


    /**
     * 根据请求的城市Id去服务器请求天气的JSON数据
     * @param weatherId
     */
    public void requestWeather(String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=4b7efcf812684ae59fdefdfa0e87130c";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            //请求失败
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false); //结束下拉刷新隐藏进度条
                    }
                });
            }
            //请求成功
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                //解析JSON数据返回一个Weather对象
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //如果状态status是ok代表请求天气成功
                        if (weather != null && "ok".equals(weather.status)){
                            //把请求的原始JSON数据缓存到SP中
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);//把解析好的Weather对象显示出来
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);//结束下拉刷新隐藏进度条
                    }
                });
            }
        });
        //每次去请求天气时也去请求图片
//        loadBingPic();
        loadBingYingPic();
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        //去服务器请求图片路径
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override//请求成功
            public void onResponse(Call call, Response response) throws IOException {
                //接收路径
                final String bingPic = response.body().string();
                //存储到SP中
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //在主线程更新界面
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    /**
     * 加载必应每日一图直接从官方地址获取图片
     */
    private void loadBingYingPic() {
        //去服务器请求图片路径
        String requestBingPic = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override//请求成功
            public void onResponse(Call call, Response response) throws IOException {
                //接收路径
                final String bingPic = response.body().string();
                //解析数据
                BingYingPic bingYingPic = Utility.handleBingYingPicResponse(bingPic);
                final String bingBasePicUrl = "http://cn.bing.com" + bingYingPic.images.get(0).bingBasePicUrl;
                //存储到SP中
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingBasePicUrl);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //在主线程更新界面
                        Glide.with(WeatherActivity.this).load(bingBasePicUrl).into(bingPicImg);
                    }
                });
            }
        });
    }

    /**
     * 处理并展示Weather实体类中的数据
     * @param weather
     */
    private void showWeatherInfo(Weather weather) {
        if (weather != null && "ok".equals(weather.status)) {
            String cityName = weather.basic.cityName;
            String updateTime = weather.basic.update.updateTime.split(" ")[1];
            String degree = weather.now.temperature + "℃";
            String weatherInfo = weather.now.more.info;
            titleCity.setText(cityName);
            titleUpdateTime.setText(updateTime);
            degreeText.setText(degree);
            weatherInfoText.setText(weatherInfo);
            forecastLayout.removeAllViews();
            for (Forecast forecast : weather.forecastList) { //循环处理未来几天的天气预报
                //创建视图, 设置视图数据, 并把视图添加到layout中
                View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
                TextView dateText = view.findViewById(R.id.date_text);
                TextView infoText = view.findViewById(R.id.info_text);
                TextView maxText = view.findViewById(R.id.max_text);
                TextView minText = view.findViewById(R.id.min_text);
                dateText.setText(forecast.date);
                infoText.setText(forecast.more.info);
                maxText.setText(forecast.temperature.max);
                minText.setText(forecast.temperature.min);
                forecastLayout.addView(view);
            }
            if (weather.aqi != null) {
                aqiText.setText(weather.aqi.city.aqi);
                pm25Text.setText(weather.aqi.city.pm25);
            }
            String comfort = "舒适度: " + weather.suggestion.comfort.info;
            String carWash = "洗车指数: " + weather.suggestion.carWash.info;
            String sport = "运动建议: " + weather.suggestion.sport.info;
            comfortText.setText(comfort);
            carWashText.setText(carWash);
            sportText.setText(sport);
            weatherLayout.setVisibility(View.VISIBLE);//把ScrollView重新变为可见
            //启动服务
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        } else {
            Toast.makeText(this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
        }
    }
}
