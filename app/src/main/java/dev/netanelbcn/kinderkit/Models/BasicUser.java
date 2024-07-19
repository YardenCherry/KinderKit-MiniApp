package dev.netanelbcn.kinderkit.Models;

import dev.netanelbcn.kinderkit.ExternalModels.Bounderies.ObjectBoundary;

public abstract class BasicUser {
    String mail;
    String uid;

    public abstract ObjectBoundary toBoundary();

    public abstract String getUid();

    public abstract BasicUser setUid(String uid);

    public abstract String getMail();

    public abstract String getPassword();

}
