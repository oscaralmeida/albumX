package com.albumx.domain.service;

import com.albumx.domain.model.Album;
import com.albumx.domain.model.User;
import com.albumx.domain.model.UserRankingPosition;
import com.albumx.domain.repository.TradeProposalRepository;
import com.albumx.domain.repository.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RankingService {

    private final UserRepository userRepository;
    private final CollectionService collectionService;
    private final TradeProposalRepository tradeProposalRepository;
    private final Album album;

    public RankingService(UserRepository userRepository,
                          CollectionService collectionService,
                          TradeProposalRepository tradeProposalRepository,
                          Album album) {
        this.userRepository = userRepository;
        this.collectionService = collectionService;
        this.tradeProposalRepository = tradeProposalRepository;
        this.album = album;
    }

    public List<UserRankingPosition> getRanking() {
        List<UserRankingPosition> positions = new ArrayList<>();

        for (User user : userRepository.findAll()) {
            int uniqueCount = collectionService.getUniqueStickerCount(user.getId());
            double percentage = (uniqueCount * 100.0) / album.getStickerCount();
            int acceptedTrades = tradeProposalRepository.countAcceptedByUserId(user.getId());
            positions.add(new UserRankingPosition(
                    user.getId(),
                    user.getName(),
                    uniqueCount,
                    percentage,
                    acceptedTrades,
                    0
            ));
        }

        positions.sort(Comparator
                .comparingDouble(UserRankingPosition::albumCompletionPercentage).reversed()
                .thenComparing(Comparator.comparingInt(UserRankingPosition::acceptedTradesCount).reversed())
                .thenComparing(p -> p.userId()));

        List<UserRankingPosition> ranked = new ArrayList<>();
        for (int i = 0; i < positions.size(); i++) {
            UserRankingPosition current = positions.get(i);
            ranked.add(new UserRankingPosition(
                    current.userId(),
                    current.userName(),
                    current.uniqueStickersCount(),
                    current.albumCompletionPercentage(),
                    current.acceptedTradesCount(),
                    i + 1
            ));
        }

        return ranked;
    }
}
