package com.utsman.backend.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

public class User {
    @Id
    private ObjectId _id;
    private String userId;
    private String email;
    private String vehiclesType;
    private String vehiclesPlat;
    private String photoProfile;
    private String token;
    private Double lat;
    private Double lon;
    private Double angle;
    private Boolean onOrder;

    public User(ObjectId _id, String userId, String email, String vehiclesType, String vehiclesPlat, String photoProfile, String token, Double lat, Double lon, Double angle, Boolean onOrder) {
        this._id = _id;
        this.userId = userId;
        this.email = email;
        this.vehiclesType = vehiclesType;
        this.vehiclesPlat = vehiclesPlat;
        this.photoProfile = photoProfile;
        this.token = token;
        this.lat = lat;
        this.lon = lon;
        this.angle = angle;
        this.onOrder = onOrder;
    }

    public String get_id() {
        return _id.toHexString();
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVehiclesType() {
        return vehiclesType;
    }

    public void setVehiclesType(String vehiclesType) {
        this.vehiclesType = vehiclesType;
    }

    public String getVehiclesPlat() {
        return vehiclesPlat;
    }

    public void setVehiclesPlat(String vehiclesPlat) {
        this.vehiclesPlat = vehiclesPlat;
    }

    public String getPhotoProfile() {
        return photoProfile;
    }

    public void setPhotoProfile(String photoProfile) {
        this.photoProfile = photoProfile == null ? null : "https://usa-latestnews.com/wp-content/plugins/all-in-one-seo-pack/images/default-user-image.png";
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getAngle() {
        return angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }

    public Boolean getOnOrder() {
        return onOrder;
    }

    public void setOnOrder(Boolean onOrder) {
        this.onOrder = onOrder;
    }
}
