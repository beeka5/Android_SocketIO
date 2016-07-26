package com.yoga.isha.sockettest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private SocketManager mSocket;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Boolean start;
    private ShakeDetector mShakeDetector;
    private TouchHandlerView mainView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainView = (TouchHandlerView)findViewById(R.id.mainView);

        mSocket = new SocketManager();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count, JSONObject obj) {
				/*
				 * The following method, "handleShakeEvent(count):" is a stub //
				 * method you would use to setup whatever you want done once the
				 * device has been shook.
				 */
                Log.d("send data", obj.toString());
//                handleShakeEvent(count);
                mSocket.sendRotData(obj);
            }
        });


        mainView.setTouchListener(new TouchHandlerView.OnTouch(){
            @Override
            public void onTouch(Boolean isScale,JSONObject obj){
                Log.d("send data", obj.toString());
                if (isScale){

                    mSocket.sendScaleData(obj);
                }else {
                    mSocket.sendMoveData(obj);
                }
            }
        });




        start = false;

        Button button = (Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Sending an object
                    JSONObject obj = new JSONObject();
                    try {

                        obj.put("reset", "true");

                    } catch (Exception e) {
                        System.out.print(e);
                    }
                    mSocket.sendResetData(obj);
                }
            });




    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        mSensorManager.unregisterListener(mShakeDetector);
    }

    public  void resetClicked(){
        Log.i("reset","reset clicked");
        JSONObject obj = new JSONObject();
        try {

            obj.put("reset", "true");

        } catch (Exception e) {
            System.out.print(e);
        }
        mSocket.sendResetData(obj);
    }


}
