package com.fuprox.noqueue.utils;

public class booking_details {

    private String co_name, b_name,time,booking_id,branch_id,Service_name,serviced;
    private int id, color;
    private long mili;

    public booking_details() {}

    public booking_details(String co_name, String branch_name, String time,String book_id, String branch_id,String Service_name,String serviced) {
        this.co_name = co_name;
        this.b_name = branch_name;
        this.time = time;
        this.booking_id=book_id;
        this.branch_id=branch_id;
        this.Service_name=Service_name;
        this.serviced=serviced;
//        this.mili = mili;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCo_name() {
        return co_name;
    }

    public void setCo_name(String name) {
        this.co_name = name;
    }

    public String getB_name() {
        return b_name;
    }

    public void setB_name(String name) {
        this.b_name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBooking_id(){ return booking_id; }
    public void setBooking_id(String b_id){ this.booking_id=b_id; }

    public String getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(String branch_id) {
        this.branch_id = branch_id;
    }

    public String getService_name() {
        return Service_name;
    }

    public void setService_name(String service_name) {
        Service_name = service_name;
    }

    public String getServiced() {
        return serviced;
    }

    public void setServiced(String serviced) {
        this.serviced = serviced;
    }
}
