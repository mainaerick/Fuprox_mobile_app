package com.example.currentplacedetailsonmap.model;

public class strings_ {

    public String url(){

        String url_str;


//            url_str="https://api.fuprox.com";
//            url_str="http://192.168.43.162:4000";
        url_str="http://68.183.89.127:4000";

        return url_str;
    }

    public String book_url(){
        String url_str;
        url_str="http://192.168.43.162:4000/book/make";


        return url_str;
    }
    public String desktopurl(){
        String urldesktop;
        urldesktop="https://desktop.fuprox.com";
        return urldesktop;
    }

    public int alarmid(){
        int id=1010;
        return id;
    }
    public int alaarmwaittime(){
        int sec=20;
        return sec;
    }
}
