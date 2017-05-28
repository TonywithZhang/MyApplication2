package com.tec.zhang.prv.databaseUtil;

import org.litepal.crud.DataSupport;

/**
 * Created by zhang on 2017/5/28.
 */

public class LogInAutoCompute extends DataSupport {
    private String airFlow;
    private String uncontrolLeakage;

    public String getAirFlow() {
        return airFlow;
    }

    public void setAirFlow(String airFlow) {
        this.airFlow = airFlow;
    }

    public String getUncontrolLeakage() {
        return uncontrolLeakage;
    }

    public void setUncontrolLeakage(String uncontrolLeakage) {
        this.uncontrolLeakage = uncontrolLeakage;
    }
}
