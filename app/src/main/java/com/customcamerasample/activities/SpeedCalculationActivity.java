package com.customcamerasample.activities;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.customcamerasample.R;
import com.customcamerasample.utils.Velocity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by shashank.rawat on 25-10-2017.
 */

public class SpeedCalculationActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    final String TAG = getClass().getName().toString();
    SensorManager mSensorManager;
    Sensor mAccelerometer;
    TableLayout accTable;
    TextView accl, spd, spd_kmph;
    Button btnStart, btnStop, btnClear;
    Timer updateTimer;
    float []linearAcceleration = new float[3];
    Velocity velocity;
    Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_calculation_screen);

        initSensor();

        accTable =(TableLayout)findViewById(R.id.country_table);

        //accl = (TextView)findViewById(R.id.accl);
        spd = (TextView)findViewById(R.id.spd);
        spd_kmph = (TextView)findViewById(R.id.spd_kmph);

        btnStart = (Button)findViewById(R.id.buttonStart);
        btnStart.setOnClickListener(this);
        btnStop = (Button)findViewById(R.id.buttonStop);
        btnStop.setOnClickListener(this);
        btnClear= (Button)findViewById(R.id.buttonClear);
        btnClear.setOnClickListener(this);
    }


    private void initSensor() {
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if(mAccelerometer == null) {
            Toast.makeText(this, "Accelerometer sensor not available", Toast.LENGTH_SHORT).show();
            finish();
        }
    }



    void fillTable(float values[]) {

        float[] val = values;
        TableRow row;
        TextView t1, t2, t3;
        //Converting to dip unit
        int dip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (float) 1, getResources().getDisplayMetrics());

        //for (int current = 0; current < CountriesList.abbreviations.length; current++) {
        row = new TableRow(this);

        t1 = new TextView(this);
        t1.setTextColor(Color.WHITE);
        t1.setBackgroundColor(Color.GRAY);
        t2 = new TextView(this);
        t2.setTextColor(Color.WHITE);
        t2.setBackgroundColor(Color.LTGRAY);
        t3 = new TextView(this);
        t3.setTextColor(Color.WHITE);
        t3.setBackgroundColor(Color.GRAY);

        t1.setText(""+val[0]);
        t2.setText(""+val[1]);
        t3.setText(""+val[2]);

        t1.setTypeface(null, 1);
        t2.setTypeface(null, 1);
        t3.setTypeface(null, 1);

        t1.setTextSize(15);
        t2.setTextSize(15);
        t3.setTextSize(15);

        t1.setWidth(150 * dip);
        t2.setWidth(150 * dip);
        t3.setWidth(150 * dip);
        t1.setPadding(20*dip, 0, 0, 0);
        row.addView(t1);
        row.addView(t2);
        row.addView(t3);

        accTable.addView(row, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

    }

    @Override
    public void onClick(View v) {
        if(v == btnStart) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            velocity = new Velocity();
            updateTimer = new Timer("velocityUpdate");
            handler = new Handler();
            updateTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    calculateAndUpdate();
                }
            }, 0, 1200);
        }else  if(v == btnStop) {
            mSensorManager.unregisterListener(this);

            displayVelocityValues();
            displayVelocityTable();
            velocity = null;
            handler = null;
            updateTimer.cancel();


        } else if(v == btnClear) {
            accTable.removeAllViews();
        }
    }



    private void displayVelocityTable() {
        try {
            accTable.removeAllViews();
            double[] vl = velocity.getVlArray();
            for(int i = 0; i<vl.length; i++) {
                /*Log.d(TAG, "v = " + vl[i] + "mps, "+(vl[i] * 3.6)+ " kmph");*/


                //float[] val = values;
                TableRow row;
                TextView t1, t2;
                //Converting to dip unit
                int dip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        (float) 1, getResources().getDisplayMetrics());

                //for (int current = 0; current < CountriesList.abbreviations.length; current++) {
                row = new TableRow(this);

                t1 = new TextView(this);
                t1.setTextColor(Color.WHITE);
                t1.setBackgroundColor(Color.GRAY);
                t2 = new TextView(this);
                t2.setTextColor(Color.WHITE);
                t2.setBackgroundColor(Color.LTGRAY);


                t1.setText(""+vl[i]);
                t2.setText(""+(vl[i] * 3.6));


                t1.setTypeface(null, 1);
                t2.setTypeface(null, 1);


                t1.setTextSize(15);
                t2.setTextSize(15);

                t1.setWidth(200 * dip);
                t2.setWidth(200 * dip);

                t1.setPadding(20*dip, 0, 0, 0);
                row.addView(t1);
                row.addView(t2);


                accTable.addView(row, new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }
        } catch(NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void displayVelocityValues() {
        try {
            double[] vl = velocity.getVlArray();
            for(int i = 0; i<vl.length; i++) {
                Log.d(TAG, "v = " + vl[i] + "mps, "+(vl[i] * 3.6)+ " kmph");
            }
        } catch(NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void calculateAndUpdate() {

        final double vel = velocity.getVelocity(linearAcceleration, System.currentTimeMillis());
        final double velKmph = vel * 3.6;
        spd.setText("v = "+ velKmph + " kmph");

        handler.post(new Runnable() {
            public void run() {

                //Log.d(getClass().getName().toString(), "Setting velocity = " + velKmph+ " kmph");
                spd.setText("v = "+ vel + " mps");
                spd_kmph.setText("v = "+ velKmph + " kmph");
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        linearAcceleration[0] = event.values[0];
        linearAcceleration[1] = event.values[1];
        linearAcceleration[2] = event.values[2];

        fillTable(linearAcceleration);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
