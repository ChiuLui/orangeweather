package com.orange.orangeweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 天气json的Basic类
 * @author 神经大条蕾弟
 * @version 1.0
 * @date 2018/04/13 19:32
 * @copyright 赵蕾
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{

        @SerializedName("loc")
        public String updateTime;

    }
}
