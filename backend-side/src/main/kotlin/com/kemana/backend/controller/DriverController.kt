package com.kemana.backend.controller

import com.kemana.backend.model.Responses
import com.kemana.backend.model.User
import com.kemana.backend.repository.DriverRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1")
class DriverController {
    @Autowired
    lateinit var driverRepository: DriverRepository

    @RequestMapping(value = ["/active"], method = [RequestMethod.GET])
    fun getActiveDriver(): Responses {
        val drivers = driverRepository.findAll()
        return Responses("OK", drivers)
    }

    @RequestMapping(value = ["/"], method = [RequestMethod.POST])
    fun saveDriver(@Valid @RequestBody driver: User): Responses {
        driver.id = UUID.randomUUID().toString()
        driverRepository.save(driver)
        return Responses("OK", listOf(driver))
    }

    @RequestMapping(value = ["/{id}"], method = [RequestMethod.GET])
    fun getDriver(@PathVariable("id") id: String): Responses {
        val driver = driverRepository.findDriverById(id)
        return Responses("OK", listOf(driver))
    }
}