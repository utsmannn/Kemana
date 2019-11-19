package com.utsman.backend.controller;


import com.utsman.backend.model.Driver;
import com.utsman.backend.model.Message;
import com.utsman.backend.repository.DriverRepository;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/driver")
public class DriverController {

    @Autowired
    private DriverRepository driverRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<Driver> getAllDriver() {
        return driverRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Driver getDriver(@PathVariable("id") ObjectId id) {
        return driverRepository.findBy_id(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Driver editDriver(@PathVariable("id") ObjectId id, @Valid @RequestBody Driver driver) {
        driver.set_id(id);
        driverRepository.save(driver);
        return driver;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Driver addDriver(@Valid @RequestBody Driver driver) {
        driver.set_id(ObjectId.get());
        driverRepository.save(driver);
        return driver;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Message deleteDriver(@PathVariable("id") ObjectId id) {
        Driver driver = driverRepository.findBy_id(id);

        try {
            driverRepository.delete(driver);
            return new Message("Delete Success");
        } catch (IllegalArgumentException e) {
            return new Message("Delete failed, maybe driver cannot find or " + e.getLocalizedMessage());
        }
    }
}
