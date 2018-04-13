package com.orange.orangeweather.db;

import org.litepal.crud.DataSupport;

/**
 * @author 神经大条蕾弟
 * @version 1.0
 * @date 2018/04/13 13:30
 * @copyright 赵蕾
 */

public class City extends DataSupport {

    private int id;

    private String cityName;

    private int cityCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }
}
