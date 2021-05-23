package com.fuprox.noqueue.utils;

public class service_details  {

     String title;
     String id;
    public service_details() {}

    public service_details(String id,String title) {
        this.title = title;
        this.id=id;
//        this.mili = mili;
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
}
