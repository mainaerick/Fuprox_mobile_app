package com.example.currentplacedetailsonmap.utils;

public class suggestion_details {
    private String title;
    private String longitude,latitude,opens,closes,id,company,company_id;
    private String ismedical;

    public suggestion_details(){}

    public suggestion_details(String title, String longitude,String latitude,String opens,String closes, String id,String company,String company_id,String ismedical) {
        this.ismedical=ismedical;
        this.title = title;
        this.longitude = longitude;
        this.latitude = latitude;
        this.opens = opens;
        this.closes = closes;
        this.id=id;
        this.company=company;
        this.company_id=company_id;
    }

    public String getIsmedical() {
        return ismedical;
    }

    public void setIsmedical(String ismedical) {
        this.ismedical = ismedical;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLongitude(){
        return longitude;
    }

    public void setLongitude(String longitude){
        this.longitude=longitude;
    }
    public String getLatitude(){
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setOpens(String opens) {
        this.opens = opens;
    }

    public String getOpens() {
        return opens;
    }

    public void setCloses(String closes) {
        this.closes = closes;
    }

    public String getCloses() {
        return closes;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompany() {
        return company;
    }
}
