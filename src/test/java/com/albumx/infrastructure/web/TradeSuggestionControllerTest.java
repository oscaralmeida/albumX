package com.albumx.infrastructure.web;

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

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TradeSuggestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String aliceId;
    private String bobId;

    @BeforeEach
    void setUp() throws Exception {
        aliceId = createUser("Alice");
        bobId = createUser("Bob");

        addSticker(aliceId, 10);
        addSticker(aliceId, 10);
        addSticker(aliceId, 25);

        addSticker(bobId, 25);
        addSticker(bobId, 25);
        addSticker(bobId, 50);
    }

    @Test
    void shouldReturnTradeSuggestionsForUser() throws Exception {
        mockMvc.perform(get("/api/users/" + aliceId + "/trade-suggestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].requesterUserId").value(aliceId))
                .andExpect(jsonPath("$[0].partnerUserId").value(bobId))
                .andExpect(jsonPath("$[0].offeredStickerNumber").value(10))
                .andExpect(jsonPath("$[0].requestedStickerNumber").value(50))
                .andExpect(jsonPath("$[0].reason").exists());
    }

    @Test
    void shouldReturnNotFoundForUnknownUser() throws Exception {
        String unknownId = "00000000-0000-0000-0000-000000000099";

        mockMvc.perform(get("/api/users/" + unknownId + "/trade-suggestions"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    void shouldReturnAllTradeSuggestions() throws Exception {
        mockMvc.perform(get("/api/trade-suggestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    private String createUser(String name) throws Exception {
        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", name))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }

    private void addSticker(String userId, int stickerNumber) throws Exception {
        mockMvc.perform(post("/api/users/" + userId + "/collection/stickers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("stickerNumber", stickerNumber))))
                .andExpect(status().isOk());
    }
}
