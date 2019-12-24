package com.utsman.kemana.backend

import com.utsman.kemana.backend.controller.PlaceController
import javafx.application.Application
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@RunWith(SpringRunner::class)
@WebMvcTest(PlaceController::class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class PlaceControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    private val searchUri = "/api/v1/place/search?q=uhamka&from=-6.1767059,106.828464&apikey=EKZhNIBtjrjeYxqdyhCMQ1kxVc_O4QGfxEJLqWt0Hp0"

    @Test
    fun searchPlace() {
        this.mockMvc.perform(MockMvcRequestBuilders.get(searchUri).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Muhammadiyah")))
                .andDo(MockMvcRestDocumentation.document("place/search-place"))
    }
}