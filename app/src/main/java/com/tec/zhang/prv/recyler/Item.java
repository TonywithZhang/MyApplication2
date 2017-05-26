package com.tec.zhang.prv.recyler;

/**
 * Created by Administrator on 2017/5/4.
 */

public class Item {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String partNumber;
    private String version;
    private String lastModified;
    private int itemImage;
    public Item(){}

    public Item(String id,String partNumber, String version, String lastModified, int itemImage) {
        this.id = id;
        this.partNumber = partNumber;
        this.version = version;
        this.lastModified = lastModified;
        this.itemImage = itemImage;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public int getItemImage() {
        return itemImage;
    }

    public void setItemImage(int itemImage) {
        this.itemImage = itemImage;
    }
}
