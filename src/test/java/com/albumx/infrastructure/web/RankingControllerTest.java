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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RankingControllerTest {

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

        for (int i = 1; i <= 5; i++) {
            addSticker(aliceId, i);
        }
        for (int i = 1; i <= 3; i++) {
            addSticker(bobId, i);
        }
    }

    @Test
    void shouldReturnOrderedRanking() throws Exception {
        mockMvc.perform(get("/api/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].position").value(1))
                .andExpect(jsonPath("$[0].userId").value(aliceId))
                .andExpect(jsonPath("$[0].uniqueStickersCount").value(5))
                .andExpect(jsonPath("$[1].position").value(2))
                .andExpect(jsonPath("$[1].userId").value(bobId))
                .andExpect(jsonPath("$[1].uniqueStickersCount").value(3));
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
