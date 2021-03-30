package com.fuprox.noqueue.model;

import android.content.Context;

import com.fuprox.noqueue.utils.Dbhelper;

public class strings_ {

    public String get_ipaddress(Context context){

        String url_str;

//            url_str="https://api.fuprox.com";
//            url_str="http://192.168.43.162:4000";
//        url_str="http://159.65.144.235:4000";
        url_str=new Dbhelper(context).get_ipaddress();

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
