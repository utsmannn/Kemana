package com.utsman.kemana.backend.controller

import com.utsman.kemana.backend.model.Responses
import com.utsman.kemana.backend.distanceTo
import com.utsman.kemana.backend.model.DriverAttribute
import com.utsman.kemana.backend.model.Position
import com.utsman.kemana.backend.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.findById
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/user")
class UserController {
    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    // save user
    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveUser(
            @RequestParam("document") document: String,
            @RequestBody user: User
    ): Responses {
        user.id = UUID.nameUUIDFromBytes(user.name?.toByteArray()).toString()
        mongoTemplate.save(user, document)
        return Responses("save in `$document` document", user)
    }

    // get user
    @RequestMapping(method = [RequestMethod.GET])
    fun getUser(
            @RequestParam("id") id: String,
            @RequestParam("document") document: String
    ): Responses {
        val user = mongoTemplate.findById<User>(id, document)
        return if (user != null) {
            Responses("ok", user)
        } else {
            Responses("ok", "not found")
        }
    }

    // edit user
    @RequestMapping(value = ["/edit"], method = [RequestMethod.PUT])
    fun editUser(
            @RequestParam("id") id: String,
            @RequestParam("document") document: String,
            @RequestBody user: User
    ): Responses {
        user.id = id
        mongoTemplate.save(user, document)
        return Responses("save in `$document` document", user)
    }

    // delete user
    @RequestMapping(value = ["/delete"], method = [RequestMethod.DELETE])
    fun deleteUser(
            @RequestParam("id") id: String,
            @RequestParam("document") document: String
    ): Responses {
        val user = mongoTemplate.findById<User>(id, document)
        return if (user != null) {
            mongoTemplate.remove(user, document)
            Responses("removed from `$document` document")
        } else {
            Responses("driver not found")
        }
    }

    // edit position
    @RequestMapping(value = ["/edit/position"], method = [RequestMethod.PUT])
    fun editPosition(
            @RequestParam("id") id: String,
            @RequestParam("document") document: String,
            @RequestBody position: Position
    ): Responses {
        val user = mongoTemplate.findById<User>(id, document)
        return if (user != null) {
            user.position = position
            mongoTemplate.save(user, document)
            Responses("save in `$document` document", user)
        } else {
            Responses("driver not found")
        }
    }

    // edit phone
    @RequestMapping(value = ["/edit/phone"], method = [RequestMethod.PUT])
    fun editPhone(
            @RequestParam("id") id: String,
            @RequestParam("document") document: String,
            @RequestBody userPhone: User
    ): Responses {
        val user = mongoTemplate.findById<User>(id, document)
        return if (user != null) {
            user.phone = userPhone.phone
            mongoTemplate.save(user, document)
            Responses("save in `$document` document", user)
        } else {
            Responses("driver not found")
        }
    }

    // edit attribute
    @RequestMapping(value = ["/edit/attribute"], method = [RequestMethod.PUT])
    fun editAttribute(
            @RequestParam("id") id: String,
            @RequestParam("document") document: String,
            @RequestBody attribute: DriverAttribute
    ): Responses {
        return if (document == "driver") {
            val user = mongoTemplate.findById<User>(id, document)

            return if (user != null) {
                user.driver_attribute = attribute
                mongoTemplate.save(user, document)
                Responses("save in `$document` document", user)
            } else {
                Responses("driver not found")
            }
        } else {
            Responses("document must be `driver`")
        }
    }

    // ---- DRIVER ACTIVATED ---- //
    // activated driver
    @RequestMapping(value = ["/driver"], method = [RequestMethod.POST])
    fun activatedDriver(@RequestParam("id") id: String): Responses {
        val user = mongoTemplate.findById<User>(id, "driver")
        return if (user != null) {
            mongoTemplate.save(user, "driver_active")
            Responses("activated", user)
        } else {
            Responses("fail")
        }
    }

    // edit driver active position
    @RequestMapping(value = ["/driver/position"], method = [RequestMethod.PUT])
    fun editPositionDriverActive(
            @RequestParam("id") id: String,
            @RequestBody position: Position
    ): Responses {
        val user = mongoTemplate.findById<User>(id, "driver_active")
        return if (user != null) {
            user.position = position
            mongoTemplate.save(user, "driver_active")
            Responses("position edited", user)
        } else {
            Responses("fail")
        }
    }

    // delete driver active
    @RequestMapping(value = ["/driver/delete"], method = [RequestMethod.DELETE])
    fun deleteDriverActive(
            @RequestParam("id") id: String
    ): Responses {
        val user = mongoTemplate.findById<User>(id, "driver_active")
        return if (user != null) {
            mongoTemplate.remove(user, "driver_active")
            Responses("removed from `driver active` document")
        } else {
            Responses("driver not found")
        }
    }

    // get all driver active by location
    @RequestMapping(value = ["/driver/active"], method = [RequestMethod.GET])
    fun getAllDriverActive(
            @RequestParam("from") coordinate: String
    ): Responses {
        val listCoordinate = coordinate.split(",")
        val lat = listCoordinate[0].toDouble()
        val lon = listCoordinate[1].toDouble()
        val mPosition = Position(lat, lon)

        val drivers = mongoTemplate.findAll<User>("driver_active")
        val nearby = drivers.filter {
            val found = it.position != null
            println(found)
            return@filter found
        }.filter {
            val result = it.position!!.distanceTo(mPosition) < 2000 // get driver active radius 2km
            println(result)
            return@filter result
        }

        return Responses("ok", nearby)
    }
}