package com.fuprox.noqueue.model;


import android.app.Application;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;


import java.net.URISyntaxException;

public class SocketInstance extends Application {
    private Socket iSocket;
    String[] dict= { };
    private static final String URL = "http://159.65.144.235:5000" ;
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            IO.Options opts = new IO.Options();
//            opts.query = "auth_token=" + authToken;
            iSocket = IO.socket(URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public Socket getSocketInstance(){
        return iSocket;
    }
}
