package com.yoga.isha.sockettest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class ShakeDetector implements SensorEventListener {

    /*
     * The gForce that is necessary to register as shake.
     * Must be greater than 1G (one earth gravity unit).
     * You can install "G-Force", by Blake La Pierre
     * from the Google Play Store and run it to see how
     *  many G's it takes to register a shake
     */
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;
    private final static double EPSILON = 0.0001;

    private final float[] mRotationMatrix = new float[16];
    private static final float SHAKE_THRESHOLD_GRAVITY = 1.5F;
    private static final int SHAKE_SLOP_TIME_MS = 100;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 1000;

    private OnShakeListener mListener;
    private long mShakeTimestamp;
    private int mShakeCount;

    public void setOnShakeListener(OnShakeListener listener) {

        mRotationMatrix[0] = 1;
        mRotationMatrix[4] = 1;
        mRotationMatrix[8] = 1;
        mRotationMatrix[12] = 1;

        this.mListener = listener;
    }

    public interface OnShakeListener {
        public void onShake(int count,JSONObject obj);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        JSONObject obj = new JSONObject();
        JSONObject data = new JSONObject();
        JSONArray arr = new JSONArray();

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // Log.i("sensorvalues","X:"+(int)(event.values[0]*180)+"Y:"+(int)(event.values[1]*180)+"Z:"+(int)(event.values[2]*180));

            // Sending an object

            try {
                arr.put(mRotationMatrix[0]);
                arr.put(mRotationMatrix[1]);
                arr.put(mRotationMatrix[2]);
                arr.put(mRotationMatrix[3]);
                arr.put(mRotationMatrix[4]);
                arr.put(mRotationMatrix[5]);
                arr.put(mRotationMatrix[6]);
                arr.put(mRotationMatrix[7]);
                arr.put(mRotationMatrix[8]);

                arr.put(mRotationMatrix[9]);
                arr.put(mRotationMatrix[10]);
                arr.put(mRotationMatrix[11]);
                arr.put(mRotationMatrix[12]);
                arr.put(mRotationMatrix[13]);
                arr.put(mRotationMatrix[14]);
                arr.put(mRotationMatrix[15]);

                data.put("valX", (int) (event.values[0] * 180));
                data.put("valY", (int) (event.values[1] * 180));
                data.put("valZ", (int) (event.values[2] * 180));
                SensorManager.getRotationMatrixFromVector(
                        mRotationMatrix, event.values);

                obj.put("sensor", "rotation");
                obj.put("data", data);

                Log.d("send data", data.toString());

            } catch (Exception e) {
                System.out.print(e);
            }


        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // This timestep's delta rotation to be multiplied by the current rotation
            // after computing it from the gyro sample data.
            if (timestamp != 0) {
                final float dT = (event.timestamp - timestamp) * NS2S;
                // Axis of the rotation sample, not normalized yet.
                float axisX = event.values[0];
                float axisY = event.values[1];
                float axisZ = event.values[2];

                // Calculate the angular speed of the sample
                float omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

                // Normalize the rotation vector if it's big enough to get the axis
                if (omegaMagnitude > EPSILON) {
                    axisX /= omegaMagnitude;
                    axisY /= omegaMagnitude;
                    axisZ /= omegaMagnitude;
                }

                // Integrate around this axis with the angular speed by the timestep
                // in order to get a delta rotation from this sample over the timestep
                // We will convert this axis-angle representation of the delta rotation
                // into a quaternion before turning it into the rotation matrix.
                float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
                float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
                deltaRotationVector[0] = sinThetaOverTwo * axisX;
                deltaRotationVector[1] = sinThetaOverTwo * axisY;
                deltaRotationVector[2] = sinThetaOverTwo * axisZ;
                deltaRotationVector[3] = cosThetaOverTwo;
            }
            timestamp = event.timestamp;
            float[] deltaRotationMatrix = new float[16];
            SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
            // User code should concatenate the delta rotation we computed with the current rotation
            // in order to get the updated rotation.
            // rotationCurrent = rotationCurrent * deltaRotationMatrix;


            try {

                arr.put(deltaRotationMatrix[0]);
                arr.put(deltaRotationMatrix[1]);
                arr.put(deltaRotationMatrix[2]);
                arr.put(deltaRotationMatrix[3]);
                arr.put(deltaRotationMatrix[4]);
                arr.put(deltaRotationMatrix[5]);
                arr.put(deltaRotationMatrix[6]);
                arr.put(deltaRotationMatrix[7]);
                arr.put(deltaRotationMatrix[8]);

                arr.put(deltaRotationMatrix[9]);
                arr.put(deltaRotationMatrix[10]);
                arr.put(deltaRotationMatrix[11]);
                arr.put(deltaRotationMatrix[12]);
                arr.put(deltaRotationMatrix[13]);
                arr.put(deltaRotationMatrix[14]);
                arr.put(deltaRotationMatrix[15]);


                obj.put("sensor", "gyro");
                obj.put("data", arr);

                Log.d("send data", arr.toString());

            } catch (Exception e) {
                System.out.print(e);
            }


        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            if (mListener != null) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float gX = x / SensorManager.GRAVITY_EARTH;
                float gY = y / SensorManager.GRAVITY_EARTH;
                float gZ = z / SensorManager.GRAVITY_EARTH;

                // gForce will be close to 1 when there is no movement.
                float gForce = (float)Math.sqrt(gX * gX + gY * gY + gZ * gZ);

                if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                    final long now = System.currentTimeMillis();
                    // ignore shake events too close to each other (500ms)
                    if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                        return;
                    }

                    // reset the shake count after 3 seconds of no shakes
                    if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                        mShakeCount = 0;
                    }

                    mShakeTimestamp = now;
                    mShakeCount++;

                    mListener.onShake(mShakeCount,obj);
                }
            }

        }
        mListener.onShake(mShakeCount,obj);
    }
}