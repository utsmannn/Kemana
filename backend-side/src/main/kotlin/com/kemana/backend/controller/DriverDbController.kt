package com.kemana.backend.controller

import com.kemana.backend.distanceTo
import com.kemana.backend.model.Position
import com.kemana.backend.model.Responses
import com.kemana.backend.model.DriverEntity
import com.kemana.backend.repository.DriverDbRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/driver_db")
class DriverDbController {
    @Autowired
    lateinit var driverDbRepository: DriverDbRepository

    @RequestMapping(value = ["/"], method = [RequestMethod.POST])
    fun saveDriver(@Valid @RequestBody driver: DriverEntity): Responses {
        driver.id = UUID.nameUUIDFromBytes(driver.email.toByteArray()).toString()
        driverDbRepository.save(driver)
        return Responses("OK", listOf(driver))
    }

    @RequestMapping(value = ["/check/{email}"], method = [RequestMethod.GET])
    fun checkRegisteredDriver(@PathVariable("email") email: String): Responses {
        val drivers = driverDbRepository.findAll().filter { it.email == email }
        return Responses("OK", drivers.isNullOrEmpty())
    }

    @RequestMapping(value = ["/registered"], method = [RequestMethod.GET])
    fun getRegisteredDriver(): Responses {
        val drivers = driverDbRepository.findAll()
        return Responses("OK", drivers)
    }

    @RequestMapping(value = ["/db/range"], method = [RequestMethod.GET])
    fun getActiveDriverByRange(
            @RequestParam("from") from: String,
            @RequestParam("distance") distance: Double
    ): Responses {
        val allDrivers = driverDbRepository.findAll()
        val listCoordinate = from.split(",")
        val lat = listCoordinate[0].toDouble()
        val lon = listCoordinate[1].toDouble()

        val mPosition = Position(lat, lon, 0.0)

        val result = allDrivers.filter { mPosition.distanceTo(it.position!!) <= distance }

        return Responses("OK", result)
    }

    @RequestMapping(value = ["/db/email"], method = [RequestMethod.GET])
    fun getActiveEmailDriver(): Responses {
        val drivers = driverDbRepository.findAll()
        val emails = drivers.map { it.email }
        return Responses("OK", emails)
    }

    @RequestMapping(value = ["/{id}"], method = [RequestMethod.GET])
    fun getDriver(@PathVariable("id") id: String): Responses {
        val driver = driverDbRepository.findDriverById(id)
        return Responses("OK", listOf(driver))
    }

    @RequestMapping(value = ["/attr/{id}"], method = [RequestMethod.GET])
    fun getAttributeDriver(@PathVariable("id") id: String): Responses {
        val driver = driverDbRepository.findDriverById(id)
        val attr = driver?.attribute
        return Responses("OK", listOf(attr))
    }

    @RequestMapping(value = [""], method = [RequestMethod.GET])
    fun getDriverByEmail(@RequestParam("email") email: String): Responses {
        val driver = driverDbRepository.findDriverByEmail(email)
        return Responses("OK", listOf(driver))
    }

    @RequestMapping(value = [""], method = [RequestMethod.PUT])
    fun editPositionByEmail(@RequestParam("email") email: String, @Valid @RequestBody position: Position): Responses {
        val driver = driverDbRepository.findDriverByEmail(email)
        driver?.position = position
        driverDbRepository.save(driver!!)
        return Responses("OK", listOf(driver))
    }

    @RequestMapping(value = ["/{id}"], method = [RequestMethod.PUT])
    fun editPosition(@PathVariable("id") id: String, @Valid @RequestBody position: Position): Responses {
        val driver = driverDbRepository.findDriverById(id)
        driver?.position = position
        driverDbRepository.save(driver!!)
        return Responses("OK", listOf(driver))
    }

    @RequestMapping(value = [""], method = [RequestMethod.DELETE])
    fun deleteByEmail(@RequestParam("email") email: String): Responses {
        val driver = driverDbRepository.findDriverByEmail(email)

        return if (driver != null) {
            driver.let { driverDbRepository.delete(it) }
            Responses("OK", listOf(null))
        } else {
            Responses("FAILED", listOf(null))
        }
    }

    @RequestMapping(value = ["/{id}"], method = [RequestMethod.DELETE])
    fun deleteDriver(@PathVariable("id") id: String): Responses {
        val driver = driverDbRepository.findDriverById(id)

        return if (driver != null) {
            driver.let { driverDbRepository.delete(it) }
            Responses("OK", listOf(null))
        } else {
            Responses("FAILED", listOf(null))
        }
    }
}