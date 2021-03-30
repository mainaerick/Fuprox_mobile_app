package com.fuprox.noqueue.utils;

public class fav_details {

    String branchname,branchid,longitude,latitude,id;
    public fav_details(String id,String branchname,String branchid,String longitude,String latitude){
        this.branchid=branchid;
        this.branchname=branchname;
        this.latitude=latitude;
        this.longitude=longitude;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public fav_details(){

    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getBranchid() {
        return branchid;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getBranchname() {
        return branchname;
    }

    public void setBranchid(String branchid) {
        this.branchid = branchid;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setBranchname(String branchname) {
        this.branchname = branchname;
    }
}
