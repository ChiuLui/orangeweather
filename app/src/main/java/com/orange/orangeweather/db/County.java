package com.orange.orangeweather.db;

import org.litepal.crud.DataSupport;

/**
 * @author 神经大条蕾弟
 * @version 1.0
 * @date 2018/04/13 13:33
 * @copyright 赵蕾
 */

public class County extends DataSupport {

    private int id;

    private String countyName;

    private String weatherId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}
