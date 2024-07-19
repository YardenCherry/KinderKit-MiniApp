package dev.netanelbcn.kinderkit.Models;

public class KidEvent {
    private String eventTitle;
    private MyDate eventDate;
    private String eId;

    private Boolean isApproved;


    public KidEvent() {
        isApproved = null;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public KidEvent setApproved(Boolean approved) {
        isApproved = approved;
        return this;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public KidEvent setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
        return this;
    }

    public MyDate getEDate() {
        return eventDate;
    }

    public KidEvent setEDate(MyDate date) {
        this.eventDate = date;
        return this;

    }

    public KidEvent setEDate(int day, int month, int year) {
        this.eventDate = new MyDate(day, month, year);
        return this;
    }

    @Override
    public String toString() {
        return "KidEvent{" +
                "eventTitle='" + eventTitle + '\'' +
                ", eventDate=" + eventDate +
                ", eId='" + eId + '\'' +
                ", isApproved=" + isApproved +
                '}';
    }

    public String geteId() {
        return this.eId;
    }

    public KidEvent seteId(String eId) {
        this.eId = eId;
        return this;
    }

    public void initEId() {
        this.eId = this.eventTitle + this.getEDate().getDay() + this.getEDate().getMonth() + this.getEDate().getYear();
    }
}
