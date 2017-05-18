package com.tec.zhang.prv.databaseUtil;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.util.Map;

/**
 * Created by 张庆德 on 2017/5/2.
 */

public class PartDetail extends DataSupport {
    //零件号
    private String partNumber;
    //项目号，或者整车号
    private String projectNumber;
    //工程成本
    private String engineeringCost;
    //供应商
    private String supplier;
    //整车风量
    private String vehicleAirflow;
    //整车号
    private String totalVehicleNumber;
    //接口特征描述
    private String bodyICD;
    //每辆车所消耗数量
    private String unit;
    //图片
    private int imagePath;
    //框架材料
    private String frameMaterial;
    //空调片材料
    private String flapMaterial;
    //密封圈材料
    private String sealMaterial;
    //折线图点的数据
    private String data;

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    public String getEngineeringCost() {
        return engineeringCost;
    }

    public void setEngineeringCost(String engineeringCost) {
        this.engineeringCost = engineeringCost;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getVehicleAirflow() {
        return vehicleAirflow;
    }

    public void setVehicleAirflow(String vehicleAirflow) {
        this.vehicleAirflow = vehicleAirflow;
    }

    public String getTotalVehicleNumber() {
        return totalVehicleNumber;
    }

    public void setTotalVehicleNumber(String totalVehicleNumber) {
        this.totalVehicleNumber = totalVehicleNumber;
    }

    public String getBodyICD() {
        return bodyICD;
    }

    public void setBodyICD(String bodyICD) {
        this.bodyICD = bodyICD;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getImagePath() {
        return imagePath;
    }

    public void setImagePath(int imagePath) {
        this.imagePath = imagePath;
    }

    public String getFrameMaterial() {
        return frameMaterial;
    }

    public void setFrameMaterial(String frameMaterial) {
        this.frameMaterial = frameMaterial;
    }

    public String getFlapMaterial() {
        return flapMaterial;
    }

    public void setFlapMaterial(String flapMaterial) {
        this.flapMaterial = flapMaterial;
    }

    public String getSealMaterial() {
        return sealMaterial;
    }

    public void setSealMaterial(String sealMaterial) {
        this.sealMaterial = sealMaterial;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
