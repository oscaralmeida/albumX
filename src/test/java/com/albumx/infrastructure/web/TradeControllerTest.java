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
class TradeControllerTest {

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

        mockMvc.perform(post("/api/users/" + aliceId + "/collection/stickers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("stickerNumber", 10))))
                .andExpect(status().isOk());
    }

    private String createUser(String name) throws Exception {
        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", name))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }

    private String createTrade(String requesterId, String targetId, int offered, int requested) throws Exception {
        String response = mockMvc.perform(post("/api/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "requesterUserId", requesterId,
                                "targetUserId", targetId,
                                "offeredStickerNumber", offered,
                                "requestedStickerNumber", requested
                        ))))
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

    @Test
    void shouldCreateAndListTradeProposal() throws Exception {
        createTrade(aliceId, bobId, 10, 50);

        mockMvc.perform(get("/api/trades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].offeredStickerNumber").value(10))
                .andExpect(jsonPath("$[0].status").value("PROPOSED"));
    }

    @Test
    void shouldRejectTradeWhenStickerNotOwned() throws Exception {
        mockMvc.perform(post("/api/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "requesterUserId", aliceId,
                                "targetUserId", bobId,
                                "offeredStickerNumber", 99,
                                "requestedStickerNumber", 50
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldRejectSelfTrade() throws Exception {
        mockMvc.perform(post("/api/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "requesterUserId", aliceId,
                                "targetUserId", aliceId,
                                "offeredStickerNumber", 10,
                                "requestedStickerNumber", 50
                        ))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAcceptTradeProposal() throws Exception {
        addSticker(bobId, 50);
        String tradeId = createTrade(aliceId, bobId, 10, 50);

        mockMvc.perform(post("/api/trades/" + tradeId + "/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("targetUserId", bobId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    void shouldRejectTradeProposal() throws Exception {
        String tradeId = createTrade(aliceId, bobId, 10, 50);

        mockMvc.perform(post("/api/trades/" + tradeId + "/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("targetUserId", bobId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void shouldRejectAcceptByRequester() throws Exception {
        addSticker(bobId, 50);
        String tradeId = createTrade(aliceId, bobId, 10, 50);

        mockMvc.perform(post("/api/trades/" + tradeId + "/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("targetUserId", aliceId))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED_TRADE_ACTION"));
    }

    @Test
    void shouldRejectAcceptWhenAlreadyFinalized() throws Exception {
        addSticker(bobId, 50);
        String tradeId = createTrade(aliceId, bobId, 10, 50);

        mockMvc.perform(post("/api/trades/" + tradeId + "/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("targetUserId", bobId))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/trades/" + tradeId + "/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("targetUserId", bobId))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("TRADE_ALREADY_FINALIZED"));
    }

    @Test
    void shouldReturnNotFoundForUnknownTrade() throws Exception {
        String unknownId = "00000000-0000-0000-0000-000000000099";

        mockMvc.perform(get("/api/trades/" + unknownId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TRADE_NOT_FOUND"));
    }
}
