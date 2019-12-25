package com.utsman.kemana.backend

import com.utsman.kemana.backend.controller.OrderController
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@RunWith(SpringRunner::class)
@WebMvcTest(OrderController::class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class OrderControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var mongoTemplate: MongoTemplate

    private val saveOrder = "/api/v1/order/save"
    private val getOrder = "/api/v1/order?id=3a388370-32a4-3e72-b87b-2316c3306639"
    private val deleteOrder = "/api/v1/order/delete?id=mfklm"

    @Test
    fun saveOrder() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.post(saveOrder)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"driver_id\" : \"drv_id\",\n" +
                        "  \"passenger_id\" : \"pasg_id\",\n" +
                        "  \"from\" : {\n" +
                        "      \"id\": \"NT_Cd95J1IADx9t-xa6WdzQnB_xQD\",\n" +
                        "      \"place_name\": \"Jalan Delima 1 Gang 3, Duren Sawit, Jakarta Timur\",\n" +
                        "      \"address_name\": \"Jalan Delima 1 Gang 3, Duren Sawit, Jakarta Timur\",\n" +
                        "      \"geometry\": [\n" +
                        "        -6.21939,\n" +
                        "        106.92863\n" +
                        "      ]\n" +
                        "  },\n" +
                        "  \"to\" : {\n" +
                        "      \"id\" : \"360qquh9-5262f23aac2c4a8c90b5c126b88302f5\",\n" +
                        "      \"place_name\" : \"Universitas Muhammadiyah Prof Dr Hamka (Uhamka)\",\n" +
                        "      \"address_name\" : \"Jalan Delima 1 Gang 3, Duren Sawit 13460\",\n" +
                        "      \"geometry\" : [ \n" +
                        "        -6.21939, \n" +
                        "        106.92863 \n" +
                        "      ]\n" +
                        "  },\n" +
                        "  \"distance\" : 320123.0\n" +
                        "}"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("distance")))
                .andDo(MockMvcRestDocumentation.document("order/save-order"))
    }

    @Test
    fun getOrder() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.get(getOrder)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(MockMvcRestDocumentation.document("order/get-order"))
    }

    @Test
    fun deleteOrder() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.delete(deleteOrder)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(MockMvcRestDocumentation.document("order/delete-order"))
    }

}
