package com.example.currentplacedetailsonmap.activities;

public class LatLong {
    double lat_;
    double long_;

    public LatLong(double lat, double longi) {
        this.lat_ = lat;
        this.long_ = longi;
    }

    public double getLat(){
        return this.lat_;
    }

    public double getLong(){
        return this.long_;
    }
}
