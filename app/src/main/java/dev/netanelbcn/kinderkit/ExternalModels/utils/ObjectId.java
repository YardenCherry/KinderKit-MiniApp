package dev.netanelbcn.kinderkit.ExternalModels.utils;

public class ObjectId {
    private String superapp;
    private String id;

    public ObjectId() {
    }

    public ObjectId(String superapp, String username) {
        this.superapp = superapp;
        this.id = username;
    }


    public String getSuperapp() {
        return this.superapp;
    }

    public void setSuperapp(String superapp) {
        this.superapp = superapp;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ObjectId{" + "id='" + id + "/n" + ", superapp='" + superapp + "/n" + '}';
    }
}
