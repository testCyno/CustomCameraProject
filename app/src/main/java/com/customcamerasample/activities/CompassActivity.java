package com.customcamerasample.activities;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.customcamerasample.utils.MyCompassView;

/**
 * Created by shashank.rawat on 25-10-2017.
 */

public class CompassActivity extends AppCompatActivity {

    private static SensorManager sensorService;
    private MyCompassView compassView;
    private Sensor accelerometerSensor, magneticSensor;
    float[] mGravity;
    float[] mGeomagnetic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        compassView = new MyCompassView(this);
        setContentView(compassView);

        sensorService = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorService.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticSensor = sensorService.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (magneticSensor != null) {
            sensorService.registerListener(mySensorEventListener, accelerometerSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
            sensorService.registerListener(mySensorEventListener, magneticSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
            Log.i("Compass MainActivity", "Registerered for ORIENTATION Sensor");
        } else {
            Log.e("Compass MainActivity", "Registerered for ORIENTATION Sensor");
            Toast.makeText(this, "MAGNETIC FIELD Sensor not found",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }



    private SensorEventListener mySensorEventListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // angle between the magnetic north direction
            // 0=North, 90=East, 180=South, 270=West
//            float azimuth = event.values[0];

            float azimuth = 0;


            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mGravity = event.values;
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagnetic = event.values;
            }
            if (mGravity != null && mGeomagnetic != null) {
                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    azimuth = orientation[0];// orientation contains: azimut, pitch and roll
                    azimuth = (float)Math.toDegrees(azimuth);
                }
            }

            compassView.updateData(azimuth);

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accelerometerSensor != null) {
            sensorService.unregisterListener(mySensorEventListener);
        }
    }
}
