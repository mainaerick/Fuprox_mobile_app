package com.fuprox.noqueue.utils;

public class branches_details {

    String title,id,open_time,close_time,lattitude,longitude,ismedical,icon_url;

    public branches_details() {}

    public branches_details(String id,String title,String open_time,String close_time,String lattitude,String longitude,String ismedical,String icon_url) {
        this.title = title;
        this.id=id;
        this.close_time=close_time;
        this.open_time=open_time;
        this.lattitude=lattitude;
        this.longitude=longitude;
        this.ismedical=ismedical;
        this.icon_url=icon_url;
    }

    public String getIsmedical() {
        return ismedical;
    }

    public void setIsmedical(String ismedical) {
        this.ismedical = ismedical;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getClose_time() {
        return close_time;
    }

    public void setClose_time(String close_time) {
        this.close_time = close_time;
    }

    public String getOpen_time() {
        return open_time;
    }

    public void setOpen_time(String open_time) {
        this.open_time = open_time;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }
}
