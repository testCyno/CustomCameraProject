package com.customcamerasample.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.customcamerasample.activities.MainActivity;

import java.io.IOException;
import java.util.List;

/**
 * Created by shashank.rawat on 10-08-2017.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private Activity activity;
    private List<Camera.Size> mSupportedPreviewSizes;
    private Camera.Size mPreviewSize;

    // Constructor that obtains context and camera
    @SuppressWarnings("deprecation")
    public CameraPreview(Context context, Camera camera) {
        super(context);
        activity = (Activity)context;
        this.mCamera = camera;
        this.mSurfaceHolder = this.getHolder();
        this.mSurfaceHolder.addCallback(this);
        this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.e("Surface Create","surface created");
        try {

            mCamera.setPreviewDisplay(surfaceHolder);
            CameraFunctionality.setCameraDisplayOrientation(activity, Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
            Camera.Parameters params = mCamera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
            params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
            params.setExposureCompensation(params.getMaxExposureCompensation());

            if(params.isAutoExposureLockSupported()) {
                params.setAutoExposureLock(false);
            }
            params.setExposureCompensation(0);
            params.setPictureFormat(ImageFormat.JPEG);
            params.setJpegQuality(100);
            mSupportedPreviewSizes = params.getSupportedPreviewSizes();
            mCamera.setParameters(params);
            mCamera.startPreview();
        } catch (IOException e) {
            // left blank for now
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.e("Surface Destroyed","surface destroyed");
        mCamera.stopPreview();
//        mCamera.release();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
                               int width, int height) {
        Log.e("Surface changed","surface changed");
        // start preview with new settings
        try {
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
            MainActivity.safeToTakePicture = true;
        } catch (Exception e) {
            // intentionally left blank for a test
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = CameraFunctionality.getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        }
    }
}
