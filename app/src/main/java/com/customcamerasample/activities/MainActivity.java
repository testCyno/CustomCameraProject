package com.customcamerasample.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.customcamerasample.R;
import com.customcamerasample.utils.AppConstants;
import com.customcamerasample.utils.CameraPreview;
import com.customcamerasample.utils.ImageFunctionalityClass;

import static android.widget.Toast.LENGTH_LONG;
import static com.customcamerasample.utils.AppConstants.hasPermissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Context context;
    private Camera mCamera;
    private FrameLayout preview;
    private ImageView capturedThumbnail;
    private String path;
    private TextView xText, yText, zText;


    // Motion sensor variables
    private SensorManager sensorMan;
    private Sensor mSensor;

    public static boolean safeToTakePicture = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        init();

        setSensorInstance();

    }




    private void init() {
        // initialising views
        context = this;
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        capturedThumbnail = (ImageView) findViewById(R.id.thumbnail);
        xText = (TextView) findViewById(R.id.xText);
        yText = (TextView) findViewById(R.id.yText);
        zText = (TextView) findViewById(R.id.zText);


        // user permissions and custom camera creation
        String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (!hasPermissions(context, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, AppConstants.CAMERA_STORAGE_PERMISSION);
        } else {
            setCustomCamera();
        }

        // setting click listners
        findViewById(R.id.button_capture).setOnClickListener(this);
        capturedThumbnail.setOnClickListener(this);
        preview.setOnClickListener(this);
    }


    private void setSensorInstance() {
        sensorMan = (SensorManager)getSystemService(SENSOR_SERVICE);
        mSensor = sensorMan.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }


    private void setCustomCamera(){
        mCamera = getCameraInstance();
        CameraPreview mCameraPreview = new CameraPreview(this, mCamera);
        preview.addView(mCameraPreview);
    }



    @Override
    protected void onResume() {
        super.onResume();
        sensorMan.registerListener(mSensorListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }



    @Override
    protected void onPause() {
        super.onPause();
        sensorMan.unregisterListener(mSensorListener);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == AppConstants.CAMERA_STORAGE_PERMISSION) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                setCustomCamera();

            } else {
                //Displaying another toast if permission is not granted
                Log.e("error", "error");
                Toast.makeText(context, "Please accept the permissions. As some part of the app may not work properly without this permission. ", LENGTH_LONG).show();
            }
        }
    }

    /**
     * Helper method to access the camera returns null if it cannot get the
     * camera or does not exist
     *
     * @return
     */
    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return camera;
    }


    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap;

            if(data != null) {
                if(mCamera != null) {
                    mCamera.startPreview();
                }
                ImageFunctionalityClass ifc = new ImageFunctionalityClass();
                bitmap = ifc.getBitmapFromImageData(context,data);
                path = ifc.saveImageToDevice(data);

                bitmap = ImageFunctionalityClass.getResizedBitmap(bitmap,800,800);

                capturedThumbnail.setImageBitmap(bitmap);

                if(path != null){
                    Intent intent = new Intent(context, AdvertisementContentScreen.class);
                    intent.putExtra("image_path",path);
                    startActivity(intent);
                    safeToTakePicture = true;
                }
            }
        }
    };



    Camera.ShutterCallback mShuterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button_capture:
//                clickPic();
                Intent speedActivityIntent = new Intent(context, SpeedCalculationActivity.class);
                startActivity(speedActivityIntent);
                break;

            case R.id.thumbnail:
                Intent intent = new Intent(context, AdvertisementContentScreen.class);
                intent.putExtra("image_path",path);
                startActivity(intent);
                break;

            case R.id.camera_preview:
                Camera.Parameters params = mCamera.getParameters();
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                mCamera.setParameters(params);
                break;
        }
    }



    private void clickPic(){
        if(safeToTakePicture) {
            mCamera.takePicture(mShuterCallback, mPicture, mPicture);
            safeToTakePicture = false;
        }
    }


    private SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            phoneShakingEffect(event);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };




    private void phoneShakingEffect(SensorEvent event){
        float[] mGravity = event.values.clone();
        // Shake detection
        float x = mGravity[0];
        float y = mGravity[1];
        float z = mGravity[2];

        xText.setText("X : "+x);
        yText.setText("Y : "+y);
        zText.setText("Z : "+z);

    }

}
