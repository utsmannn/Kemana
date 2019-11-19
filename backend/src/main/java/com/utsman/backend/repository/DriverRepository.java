package com.utsman.backend.repository;

import com.utsman.backend.model.Driver;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DriverRepository extends MongoRepository<Driver, String> {
    Driver findByEmail(String email);
    Driver findBy_id(ObjectId _id);
}
