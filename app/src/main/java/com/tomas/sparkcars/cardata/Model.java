package com.tomas.sparkcars.cardata;

import android.graphics.Bitmap;

public class Model {
    int id;
    String title;
    String photoUrl;
    Bitmap photo;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}
