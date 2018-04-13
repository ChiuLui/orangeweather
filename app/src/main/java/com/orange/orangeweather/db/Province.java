package com.orange.orangeweather.db;

import org.litepal.crud.DataSupport;

/**
 * 省份表
 * @author 神经大条蕾弟
 * @version 1.0
 * @date 2018/04/13 13:25
 * @copyright 赵蕾
 */

public class Province extends DataSupport {

    private int id;

    private String provinceName;

    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
