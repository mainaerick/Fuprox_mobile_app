package com.fuprox.noqueue.model;


import android.app.Application;
import android.util.Log;


import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketInstance extends Application {
    String[] dict= { };
    Socket iSocket;
    private static final String URL = "http://159.65.144.235:5000";
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            IO.Options opts = new IO.Options();
//            opts.query = "auth_token=" + authToken;
            iSocket = IO.socket(URL);
            Log.e("TAG", "onCreate: socket ok");
        } catch (URISyntaxException e) {
            Log.e("TAG", "onCreate: socket_instance "+e.getMessage() );
            throw new RuntimeException(e);
        }
    }
    public Socket getSocketInstance(){
        return iSocket;
    }
}
