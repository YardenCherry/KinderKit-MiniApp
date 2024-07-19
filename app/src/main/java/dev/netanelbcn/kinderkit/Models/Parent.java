package dev.netanelbcn.kinderkit.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dev.netanelbcn.kinderkit.ExternalModels.Bounderies.ObjectBoundary;
import dev.netanelbcn.kinderkit.ExternalModels.utils.CreatedBy;
import dev.netanelbcn.kinderkit.ExternalModels.utils.Location;
import dev.netanelbcn.kinderkit.ExternalModels.utils.ObjectId;
import dev.netanelbcn.kinderkit.ExternalModels.utils.UserId;

public class Parent extends BasicUser {
    //private String uid;
    private String name;
    private String phone;
    private String address;
    private String password;
    private double latitude;
    private double longitude;
    private ArrayList<Kid> kids;


    private ArrayList<String> kidsId;
    private String parentMail;


    public Parent() {
        this.kids = new ArrayList<>();
        this.kidsId = new ArrayList<>();

    }

    public Parent(String uid, String name, String phone, String mail, String address, String password, double latitude, double longitude) {
        super.uid = uid;//
        this.name = name;//
        this.phone = phone;//
        super.mail = mail;//
        this.address = address;//
        this.password = password;//
        this.latitude = latitude;
        this.longitude = longitude;
        this.kids = new ArrayList<>();
        this.kidsId = new ArrayList<>();

        // this.parentMail = null;


    }

    public ArrayList<Kid> getKids() {
        return kids;
    }


    // Constructor for common fields

    public Parent setKids(ArrayList<Kid> kids) {
        this.kids = kids;
        return this;
    }

    // Getters and setters
    @Override
    public String getUid() {
        return super.uid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    @Override
    public String getMail() {
        return super.mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public BasicUser setUid(String uid) {
        super.uid = uid;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public Parent setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public void addNewKid(Kid kid) {
        this.kidsId.add(kid.getUid());
        this.kids.add(kid);
        this.kids.sort((o1, o2) -> o2.getAge() - o1.getAge());
    }

    public void removeKid(Kid kid) {
        this.kidsId.remove(kid.getUid());
        this.kids.remove(kid);
    }


    public ArrayList<String> getKidsId() {
        return kidsId;
    }

    public Parent setKidsId(ArrayList<String> kidsId) {
        this.kidsId = kidsId;
        return this;
    }

    @Override
    public String toString() {
        return "Parent{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", password='" + password + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", kids=" + kids +
                ", kidsId=" + kidsId +
                ", parentMail='" + parentMail + '\'' +
                '}';
    }

    public double getLongitude() {
        return longitude;
    }

    public Parent setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    @Override
    public ObjectBoundary toBoundary() {
        ObjectBoundary objectBoundary = new ObjectBoundary();
        objectBoundary.setType(this.getClass().getSimpleName());
        objectBoundary.setAlias(this.getPassword());
        objectBoundary.setLocation(new Location(this.getLatitude(), this.getLongitude()));
        objectBoundary.setActive(true);
        CreatedBy user = new CreatedBy();
        objectBoundary.setObjectId(new ObjectId()); //
        user.setUserId((new UserId()).setEmail(this.getMail()));
        objectBoundary.setCreatedBy(user);
        Map<String, Object> details = new HashMap<>();
        details.put("numberOfChildren", this.getKids().size());
        details.put("name", this.getName());
        details.put("phone", this.getPhone());
        details.put("uid", this.getUid());
        details.put("latitude", this.getLatitude());
        details.put("longitude", this.getLongitude());
        details.put("mail", this.getMail());
        details.put("address", this.getAddress());
        details.put("password", this.getPassword());
        details.put("kidsId", this.getKidsId());
        details.put("kids", this.getKids());
        objectBoundary.setObjectDetails(details);
        return objectBoundary;
    }


    // public ObjectBoundary toBoundary();
    //  public void toKids(ObjectBoundary o);
}



