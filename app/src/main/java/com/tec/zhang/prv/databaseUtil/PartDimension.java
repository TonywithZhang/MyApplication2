package com.tec.zhang.prv.databaseUtil;

import org.litepal.crud.DataSupport;

/**
 * Created by zhang on 2017/5/26.
 */

public class PartDimension extends DataSupport {
    private String partNum;
    private String length;
    private String width;
    private String height;

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getPartNum() {
        return partNum;
    }

    public void setPartNum(String partNum) {
        this.partNum = partNum;
    }
}
