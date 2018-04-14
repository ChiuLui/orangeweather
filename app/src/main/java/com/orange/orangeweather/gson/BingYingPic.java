package com.orange.orangeweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 必应每日一图的JSON解析
 * @author 神经大条蕾弟
 * @version 1.0
 * @date 2018/04/14 17:29
 * @copyright 赵蕾
 */

public class BingYingPic {

    @SerializedName("images")
    public List<BaseBingPic> images;

    public class BaseBingPic {
        @SerializedName("url")
        public String bingBasePicUrl;//这里的链接还需要加上微软的http://cn.bing.com在前面

        @SerializedName("enddate")
        public String endDate;//最后更新的时间

        @SerializedName("startdate")
        public String startDate;

    }

}
