package dev.netanelbcn.kinderkit.ExternalModels.Bounderies;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dev.netanelbcn.kinderkit.ExternalModels.utils.CreatedBy;
import dev.netanelbcn.kinderkit.ExternalModels.utils.Location;
import dev.netanelbcn.kinderkit.ExternalModels.utils.ObjectId;

public class ObjectBoundary {

    private ObjectId objectId; //server generated
    private String type; //parent
    private String alias; // should be password
    private Location location; //need to add
    private Boolean active;
    private Date creationTimestamp;//server generated
    private CreatedBy createdBy;
    private Map<String, Object> objectDetails; //kids map

    public ObjectBoundary() {
        objectDetails = new HashMap<>();
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public CreatedBy getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CreatedBy createdBy) {
        this.createdBy = createdBy;
    }

    public Map<String, Object> getObjectDetails() {
        return objectDetails;
    }

    public void setObjectDetails(Map<String, Object> objectDetails) {
        this.objectDetails = objectDetails;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "ObjectBoundary {objectId=" + objectId + ", type=" + type + ", alias=" + alias + ", active=" + active
                + ", location= " + location + ", creationTimestamp=" + creationTimestamp + ", createdBy=" + createdBy
                + ", objectDetails=" + objectDetails + "}";

    }
}
