package com.customcamerasample.activities;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shashank.rawat on 12-10-2017.
 */

public class AdvertisementContentPojo implements Parcelable{

    private Bitmap advImage;

    public AdvertisementContentPojo(){

    }


    protected AdvertisementContentPojo(Parcel in) {
        advImage = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<AdvertisementContentPojo> CREATOR = new Creator<AdvertisementContentPojo>() {
        @Override
        public AdvertisementContentPojo createFromParcel(Parcel in) {
            return new AdvertisementContentPojo(in);
        }

        @Override
        public AdvertisementContentPojo[] newArray(int size) {
            return new AdvertisementContentPojo[size];
        }
    };

    public Bitmap getAdvImage() {
        return advImage;
    }

    public void setAdvImage(Bitmap advImage) {
        this.advImage = advImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(advImage, i);
    }
}
