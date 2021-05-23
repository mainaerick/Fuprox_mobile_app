package com.fuprox.noqueue.utils;

public class institution_details {

     String title;
     String id;
     String icon_url;


    public institution_details() {}

    public institution_details(String id,String title,String icon_url) {
        this.title = title;
        this.id=id;
        this.icon_url=icon_url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon_url() { return icon_url; }
    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }
}

