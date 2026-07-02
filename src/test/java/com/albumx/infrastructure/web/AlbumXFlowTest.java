package com.albumx.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
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
class AlbumXFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCompleteMvpFlow() throws Exception {
        String aliceId = createUser("Alice");
        String bobId = createUser("Bob");

        addSticker(aliceId, 10);
        addSticker(aliceId, 10);
        addSticker(aliceId, 25);

        mockMvc.perform(get("/api/users/" + aliceId + "/collection/duplicates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].stickerNumber").value(10));

        mockMvc.perform(post("/api/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "requesterUserId", aliceId,
                                "targetUserId", bobId,
                                "offeredStickerNumber", 10,
                                "requestedStickerNumber", 50
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PROPOSED"));

        mockMvc.perform(get("/api/trades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/users/" + aliceId + "/collection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries[?(@.stickerNumber==10)].quantity").value(2));
    }

    @Test
    void shouldCompleteMvpFlowWithAcceptAndUpdatedCollections() throws Exception {
        String aliceId = createUser("Alice");
        String bobId = createUser("Bob");

        addSticker(aliceId, 10);
        addSticker(aliceId, 10);
        addSticker(bobId, 50);

        String tradeResponse = mockMvc.perform(post("/api/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "requesterUserId", aliceId,
                                "targetUserId", bobId,
                                "offeredStickerNumber", 10,
                                "requestedStickerNumber", 50
                        ))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String tradeId = objectMapper.readTree(tradeResponse).get("id").asText();

        mockMvc.perform(post("/api/trades/" + tradeId + "/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("targetUserId", bobId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        mockMvc.perform(get("/api/users/" + aliceId + "/collection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries[?(@.stickerNumber==10)].quantity").value(1))
                .andExpect(jsonPath("$.entries[?(@.stickerNumber==50)].quantity").value(1));

        mockMvc.perform(get("/api/users/" + bobId + "/collection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries[?(@.stickerNumber==10)].quantity").value(1))
                .andExpect(jsonPath("$.entries[?(@.stickerNumber==50)]").isEmpty());
    }

    @Test
    void shouldCompleteEvolution2FlowWithSuggestionsAndRanking() throws Exception {
        String aliceId = createUser("Alice");
        String bobId = createUser("Bob");

        addSticker(aliceId, 10);
        addSticker(aliceId, 10);
        addSticker(bobId, 50);
        addSticker(bobId, 50);

        mockMvc.perform(get("/api/users/" + aliceId + "/trade-suggestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].offeredStickerNumber").value(10))
                .andExpect(jsonPath("$[0].requestedStickerNumber").value(50));

        String tradeResponse = mockMvc.perform(post("/api/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "requesterUserId", aliceId,
                                "targetUserId", bobId,
                                "offeredStickerNumber", 10,
                                "requestedStickerNumber", 50
                        ))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String tradeId = objectMapper.readTree(tradeResponse).get("id").asText();

        mockMvc.perform(post("/api/trades/" + tradeId + "/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("targetUserId", bobId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        mockMvc.perform(get("/api/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.userId=='" + aliceId + "')].acceptedTradesCount").value(1))
                .andExpect(jsonPath("$[?(@.userId=='" + bobId + "')].acceptedTradesCount").value(1));
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
