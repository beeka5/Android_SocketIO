package com.yoga.isha.sockettest;

import android.provider.SyncStateContract;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by Macist on 7/20/16.
 */


public class SocketManager {

    public static final String CHAT_SERVER_URL = "";

    private Socket mSocket;

    public Socket getSocket() {
        return mSocket;
    }


    public SocketManager(){
        try {
            mSocket = IO.socket(CHAT_SERVER_URL);
            mSocket.connect();
            mSocket.emit("connectUser", "bikash");

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public SocketManager(String serverUrl){
        try {
            mSocket = IO.socket(serverUrl);
            mSocket.connect();
            mSocket.emit("connectUser", "bikash");

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendRotData(JSONObject obj){

        mSocket.emit("rotateObj", "bikash", obj);
    }
    public void  sendScaleData(JSONObject obj){

        mSocket.emit("scaleObj", "bikash", obj);
    }
    public void sendMoveData(JSONObject obj){

        mSocket.emit("moveObj", "bikash", obj);
    }
    public void sendResetData(JSONObject obj){

        mSocket.emit("reset", "bikash", obj);
    }
}
