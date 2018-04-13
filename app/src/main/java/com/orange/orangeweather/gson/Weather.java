package com.orange.orangeweather.gson;

import java.util.List;

/**
 * 总的实体类
 * @author 神经大条蕾弟
 * @version 1.0
 * @date 2018/04/13 20:28
 * @copyright 赵蕾
 */

public class Weather {

    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    public List<Forecast> forecastList;

}
