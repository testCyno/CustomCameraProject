package com.customcamerasample.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.customcamerasample.R;
import com.customcamerasample.utils.ImageFunctionalityClass;

import java.io.IOException;

/**
 * Created by shashank.rawat on 12-10-2017.
 */

public class AdvertisementContentScreen extends AppCompatActivity {

    private ImageView clickedPic;
    private String imagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisment_content);

        imagePath = getIntent().getStringExtra("image_path");
        Log.e("IMAGE PATH",""+imagePath);

        ImageFunctionalityClass imf = new ImageFunctionalityClass();
        Bitmap bmp = imf.getImageFromPath(imagePath);
        try {
            bmp  = imf.modifyOrientation(bmp, imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        viewInitialisation();

        clickedPic.setImageBitmap(bmp);
    }

    private void viewInitialisation() {
        clickedPic = (ImageView) findViewById(R.id.clickedImage);
    }
}
