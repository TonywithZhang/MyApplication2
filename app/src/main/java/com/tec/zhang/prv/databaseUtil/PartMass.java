package com.tec.zhang.prv.databaseUtil;

import org.litepal.crud.DataSupport;

/**
 * Created by zhang on 2017/6/1.
 */

public class PartMass extends DataSupport{
    private String partId;
    private String partNum;
    private String mass;

    public String getId() {
        return partId;
    }

    public void setId(String id) {
        this.partId = id;
    }

    public String getPartNum() {
        return partNum;
    }

    public void setPartNum(String partNum) {
        this.partNum = partNum;
    }

    public String getMass() {
        return mass;
    }

    public void setMass(String mass) {
        this.mass = mass;
    }
}
