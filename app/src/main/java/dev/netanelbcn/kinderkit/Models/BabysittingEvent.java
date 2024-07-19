package dev.netanelbcn.kinderkit.Models;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import dev.netanelbcn.kinderkit.ExternalModels.Bounderies.ObjectBoundary;
import dev.netanelbcn.kinderkit.ExternalModels.utils.CreatedBy;
import dev.netanelbcn.kinderkit.ExternalModels.utils.Location;
import dev.netanelbcn.kinderkit.ExternalModels.utils.ObjectId;
import dev.netanelbcn.kinderkit.ExternalModels.utils.UserId;

public class BabysittingEvent {
    private String messageId;
    private String messageText;
    private String selectedDate;
    private String parentUid;
    private String babysitterUid;
    private Boolean status;
    private String mailParent;

    public BabysittingEvent() {
    }

    // Constructor with parameters
    public BabysittingEvent(String parentUid, String babysitterUid, String messageText, String selectedDate, String mailParent) {
        this.messageText = messageText;
        this.selectedDate = selectedDate;
        this.parentUid = parentUid;
        this.babysitterUid = babysitterUid;
        this.status = null;
        this.mailParent = mailParent;
    }

    public String getMessageId() {
        return messageId;
    }

    public BabysittingEvent setMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public String getParentUid() {
        return parentUid;
    }

    public BabysittingEvent setParentUid(String parentUid) {
        this.parentUid = parentUid;
        return this;
    }

    public KidEvent toKidEvent() {

        String[] dateParts = this.getSelectedDate().split("-");
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int year = Integer.parseInt(dateParts[2]);
        MyDate myDate = new MyDate(day, month, year);
        return new KidEvent().setEventTitle("Babysitter Event:\n" + this.getMessageText())
                .setEDate(myDate).seteId(this.getMessageId()).setApproved(this.status);
    }

    public String getBabysitterUid() {
        return babysitterUid;
    }

    public void setBabysitterUid(String babysitterUid) {
        this.babysitterUid = babysitterUid;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public BabysittingEvent setStatus(boolean status) {
        this.status = status;
        return this;
    }

    public String getMailParent() {
        return mailParent;
    }

    public void setMailParent(String mailParent) {
        this.mailParent = mailParent;
    }

    public ObjectBoundary toBoundary() {
        ObjectBoundary objectBoundary = new ObjectBoundary();
        objectBoundary.setObjectId(new ObjectId());
        objectBoundary.getObjectId().setId(this.messageId);
        objectBoundary.setType(this.getClass().getSimpleName());
        objectBoundary.setAlias(this.getBabysitterUid());
        objectBoundary.setLocation(new Location(0, 0));
        objectBoundary.setActive(true);
        CreatedBy user = new CreatedBy();
        user.setUserId((new UserId()).setEmail(this.getMailParent()));
        objectBoundary.setCreatedBy(user);
        Map<String, Object> details = new HashMap<>();
        details.put("messageId", this.messageId);
        details.put("messageText", this.messageText);
        details.put("selectedDate", this.selectedDate);
        details.put("parentUid", this.parentUid);
        details.put("babysitterUid", this.babysitterUid);
        details.put("status", this.status);
        objectBoundary.setObjectDetails(details);
        return objectBoundary;
    }

    public BabysittingEvent toBabysittingEvent(String json) {
        BabysittingEvent babysittingEvent = new Gson().fromJson(json, BabysittingEvent.class);
        return babysittingEvent;
    }

    @Override
    public String toString() {
        return "BabysittingEvent{" +
                "messageId='" + messageId + '\'' +
                ", messageText='" + messageText + '\'' +
                ", selectedDate='" + selectedDate + '\'' +
                ", parentUid='" + parentUid + '\'' +
                ", babysitterUid='" + babysitterUid + '\'' +
                ", status=" + status +
                ", mailParent='" + mailParent + '\'' +
                '}';
    }
}
