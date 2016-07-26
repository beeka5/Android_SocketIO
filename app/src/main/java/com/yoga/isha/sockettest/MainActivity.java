package com.yoga.isha.sockettest;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vstechlab.easyfonts.EasyFonts;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private SocketManager mSocket;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Boolean start;
    private ShakeDetector mShakeDetector;
    private TouchHandlerView mainView;
    EditText ipFld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ipFld  = (EditText) findViewById(R.id.editfield);
        ipFld.setTypeface(EasyFonts.robotoThin(this));
        ipFld.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(ipFld, InputMethodManager.SHOW_IMPLICIT);

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


        Button connectBtn = (Button)findViewById(R.id.connect);

        connectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                mSocket = new SocketManager("http://"+ipFld.getText().toString()+":3000/");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(ipFld.getWindowToken(), 0);
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
