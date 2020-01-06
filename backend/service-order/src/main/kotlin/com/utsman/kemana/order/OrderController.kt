package com.utsman.kemana.order

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class OrderController {

    fun requestOrder(): Responses {
        

        return Responses("ok", "ok")
    }
}