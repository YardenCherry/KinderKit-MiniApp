package dev.netanelbcn.kinderkit.Models;

import java.util.UUID;

public class MyPhoto {
    private String pId;
    private String photoUri;

    public MyPhoto() {
        pId = UUID.randomUUID().toString().toLowerCase();
    }

    @Override
    public String toString() {
        return "MyPhoto{" +
                "pId='" + pId + '\'' +
                ", photoUri=" + photoUri +
                '}';
    }

    public String getpId() {
        return pId;
    }

    public MyPhoto setpId(String pId) {
        this.pId = pId;
        return this;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public MyPhoto setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
        return this;
    }
}
