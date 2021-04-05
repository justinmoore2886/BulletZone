package edu.unh.cs.cs619.bulletzone.Controls;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Context;
import java.lang.UnsupportedOperationException;

import static android.content.Context.SENSOR_SERVICE;

/**
 * <h1> ShakeSensor Class! </h1>
 * The ShakeSensor class defines what will occur what happens when the phone is shaken.
 *
 * @author Luke McIntire
 * @version 1.0
 * @since 10/31/2018
 */
public class ShakeSensor implements SensorEventListener
{
    private static final int FORCE_THRESHOLD = 100;
    private static final int TIME_THRESHOLD = 100;
    private static final int SHAKE_TIMEOUT = 500;
    private static final int SHAKE_DURATION = 1000;
    private static final int SHAKE_COUNT = 0;
    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;


    private SensorManager mSensorMgr;
    private float mLastX=-1.0f, mLastY=-1.0f, mLastZ=-1.0f;
    private long mLastTime;
    private OnShakeListener mShakeListener;
    private Context mContext;
    private int mShakeCount = 0;
    private long mLastShake;
    private long mLastForce;

    public interface OnShakeListener
    {
        public void onShake();
    }

    /**
     * This is the constuctor. It creates what we need to detect shakes.
     * @param context
     */
    public ShakeSensor(Context context)
    {
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mContext = context;
        resume();
    }

    /**
     * This method sets the shakeListener.
     * @param listener This is the listener for shakes.
     */
    public void setOnShakeListener(OnShakeListener listener)
    {
        mShakeListener = listener;
    }

    /**
     * This method resumes the ShakeSensor. A lot of library mumbo jumbo.
     */
    public void resume() {
        mSensorMgr = (SensorManager)mContext.getSystemService(SENSOR_SERVICE);
        if (mSensorMgr == null) {
            throw new UnsupportedOperationException("Sensors not supported");
        }
        boolean supported = mSensorMgr.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if (!supported) {
            mSensorMgr.unregisterListener(this, mAccelerometer);
            throw new UnsupportedOperationException("Accelerometer not supported");
        }
    }

    /**
     * This method pauses the ShakeSensor. A lot of library mumbo jumbo.
     */
    public void pause() {
        if (mSensorMgr != null) {
            mSensorMgr.unregisterListener(this, mAccelerometer);
            mSensorMgr = null;
        }
    }

    /**
     * This method increments accuracy of the shake sensor of the phone.
     */
    public void onAccuracyChanged(Sensor sensor, int accuracy) { accuracy += 1; }

    /**
     * This defines what happens when the sensor is changed (shaken). A lot of library mumbo jumbo.
     */
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor != mAccelerometer) return;
        long now = System.currentTimeMillis();

        if ((now - mLastForce) > SHAKE_TIMEOUT) {
            mShakeCount = 0;
        }

        if ((now - mLastTime) > TIME_THRESHOLD) {
            long diff = now - mLastTime;
            float speed = Math.abs(event.values[0] + event.values[1] + event.values[2] - mLastX - mLastY - mLastZ) / diff * 10000;
            if (speed > FORCE_THRESHOLD) {
                if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
                    mLastShake = now;
                    mShakeCount = 0;
                    if (mShakeListener != null) {
                        mShakeListener.onShake();
                    }
                }
                mLastForce = now;
            }
            mLastTime = now;
            mLastX = event.values[0];
            mLastY = event.values[1];
            mLastZ = event.values[2];
        }
    }

}
