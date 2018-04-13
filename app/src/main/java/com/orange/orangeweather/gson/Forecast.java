package com.orange.orangeweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author 神经大条蕾弟
 * @version 1.0
 * @date 2018/04/13 20:19
 * @copyright 赵蕾
 */

public class Forecast {

    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature{

        public String max;

        public String min;

    }

    public class More{

        @SerializedName("txt_d")
        public String info;

    }

}
