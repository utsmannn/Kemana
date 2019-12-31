package com.utsman.kemana.backend

import com.utsman.kemana.backend.controller.DirectionController
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@RunWith(SpringRunner::class)
@WebMvcTest(DirectionController::class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class DirectionControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    private val getDirectionUri = "/api/v1/direction?from=-6.1767059,106.828464&to=-6.21939,106.92863&token=pk.eyJ1Ijoia3VjaW5nYXBlcyIsImEiOiJjazFjZXB4aDIyb3gwM2Nxajlza2c2aG8zIn0.htmYJKp9aaJnh-JhWZA85Q"

    @Test
    fun getDirectionTest() {
        this.mockMvc.perform(MockMvcRequestBuilders.get(getDirectionUri)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("distance")))
                .andDo(MockMvcRestDocumentation.document("direction/get-direction"))
    }
}