package com.orange.orangeweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author 神经大条蕾弟
 * @version 1.0
 * @date 2018/04/13 19:59
 * @copyright 赵蕾
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    public class Comfort{

        @SerializedName("txt")
        public String info;

    }

    public class CarWash{

        @SerializedName("txt")
        public String info;

    }

    public class Sport{

        @SerializedName("txt")
        public String info;

    }


}
