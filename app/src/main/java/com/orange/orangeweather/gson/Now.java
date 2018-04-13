package com.orange.orangeweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author 神经大条蕾弟
 * @version 1.0
 * @date 2018/04/13 19:54
 * @copyright 赵蕾
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{

        @SerializedName("txt")
        public String info;

    }

}
