package com.tec.zhang.prv.databaseUtil;

import org.litepal.crud.DataSupport;

/**
 * Created by zhang on 2017/5/23.
 */

public class LineData extends DataSupport {
    private String partNum;
    private String x0;
    private String x25;
    private String x50;
    private String x75;
    private String x100;
    private String x125;
    private String x150;
    private String x175;
    private String x200;

    public String getPartNum() {
        return partNum;
    }

    public void setPartNum(String partNum) {
        this.partNum = partNum;
    }

    public String getX0() {
        return x0;
    }

    public void setX0(String x0) {
        this.x0 = x0;
    }

    public String getX25() {
        return x25;
    }

    public void setX25(String x25) {
        this.x25 = x25;
    }

    public String getX50() {
        return x50;
    }

    public void setX50(String x50) {
        this.x50 = x50;
    }

    public String getX75() {
        return x75;
    }

    public void setX75(String x75) {
        this.x75 = x75;
    }

    public String getX100() {
        return x100;
    }

    public void setX100(String x100) {
        this.x100 = x100;
    }

    public String getX125() {
        return x125;
    }

    public void setX125(String x125) {
        this.x125 = x125;
    }

    public String getX150() {
        return x150;
    }

    public void setX150(String x150) {
        this.x150 = x150;
    }

    public String getX175() {
        return x175;
    }

    public void setX175(String x175) {
        this.x175 = x175;
    }

    public String getX200() {
        return x200;
    }

    public void setX200(String x200) {
        this.x200 = x200;
    }
}
