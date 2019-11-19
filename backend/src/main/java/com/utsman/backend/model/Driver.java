package com.utsman.backend.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "driver")
public class Driver extends User {

    public Driver(ObjectId _id, String userId, String email, String vehiclesType, String vehiclesPlat, String photoProfile, String token, Double lat, Double lon, Double angle, Boolean onOrder) {
        super(_id, userId, email, vehiclesType, vehiclesPlat, photoProfile, token, lat, lon, angle, onOrder);
    }
}
