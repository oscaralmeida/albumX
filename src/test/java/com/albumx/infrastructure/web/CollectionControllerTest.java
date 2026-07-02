package com.albumx.infrastructure.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CollectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String userId;

    @BeforeEach
    void createUser() throws Exception {
        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "Colecionador"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        userId = objectMapper.readTree(response).get("id").asText();
    }

    @Test
    void shouldAddStickerAndListCollection() throws Exception {
        mockMvc.perform(post("/api/users/" + userId + "/collection/stickers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("stickerNumber", 10))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(1));

        mockMvc.perform(post("/api/users/" + userId + "/collection/stickers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("stickerNumber", 10))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(2));

        mockMvc.perform(get("/api/users/" + userId + "/collection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries", hasSize(1)))
                .andExpect(jsonPath("$.entries[0].stickerNumber").value(10))
                .andExpect(jsonPath("$.entries[0].quantity").value(2));
    }

    @Test
    void shouldRejectInvalidStickerNumber() throws Exception {
        mockMvc.perform(post("/api/users/" + userId + "/collection/stickers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("stickerNumber", 9999))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404ForUnknownUser() throws Exception {
        mockMvc.perform(get("/api/users/" + UUID.randomUUID() + "/collection"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldListDuplicates() throws Exception {
        for (int i = 0; i < 2; i++) {
            mockMvc.perform(post("/api/users/" + userId + "/collection/stickers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("stickerNumber", 42))))
                    .andExpect(status().isOk());
        }
        mockMvc.perform(post("/api/users/" + userId + "/collection/stickers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("stickerNumber", 7))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/" + userId + "/collection/duplicates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].stickerNumber").value(42))
                .andExpect(jsonPath("$[0].quantity").value(2));
    }
}
