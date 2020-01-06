package com.utsman.kemana.backend

import com.utsman.kemana.backend.controller.UserController
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
@WebMvcTest(UserController::class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class UserControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var mongoTemplate: MongoTemplate

    private val saveUserUri = "/api/v1/user/save?document=driver"
    private val getUserUri = "/api/v1/user?document=driver&id=1281d0ac-7a74-3b91-950f-f52a02862cda"
    private val editUserUri = "/api/v1/user/edit?document=driver&id=1281d0ac-7a74-3b91-950f-f52a02862cda"
    private val deleteUserUri = "/api/v1/user/delete?document=driver&id=1281d0ac-7a74-3b91-950f-f52a02862cda"

    @Test
    fun saveUser() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.post(saveUserUri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"name\" : \"marjuki\",\n" +
                        "  \"email\" : \"email@marjuki.com\",\n" +
                        "  \"photo\" : \"photoUrl\"\n" +
                        "}"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("name")))
                .andDo(MockMvcRestDocumentation.document("user/save-user"))
    }

    @Test
    fun getUser() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.get(getUserUri)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(MockMvcRestDocumentation.document("user/get-user"))
    }

    @Test
    fun editUser() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.put(editUserUri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"photo\" : \"http://example.jpg\"\n" +
                        "}"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("name")))
                .andDo(MockMvcRestDocumentation.document("user/edit-user"))
    }

    @Test
    fun deleteUser() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.delete(deleteUserUri)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(MockMvcRestDocumentation.document("user/delete-user"))
    }
}