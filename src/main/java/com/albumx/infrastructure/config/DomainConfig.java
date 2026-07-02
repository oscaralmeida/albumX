package com.albumx.infrastructure.config;

import com.albumx.domain.model.Album;
import com.albumx.domain.service.CollectionService;
import com.albumx.domain.service.RankingService;
import com.albumx.domain.service.TradeService;
import com.albumx.domain.service.TradeSuggestionService;
import com.albumx.domain.service.UserService;
import com.albumx.domain.repository.CollectionRepository;
import com.albumx.domain.repository.TradeProposalRepository;
import com.albumx.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public Album album(AlbumProperties albumProperties) {
        return new Album(albumProperties.getStickerCount());
    }

    @Bean
    public UserService userService(UserRepository userRepository,
                                   CollectionRepository collectionRepository) {
        return new UserService(userRepository, collectionRepository);
    }

    @Bean
    public CollectionService collectionService(CollectionRepository collectionRepository,
                                               UserRepository userRepository,
                                               Album album) {
        return new CollectionService(collectionRepository, userRepository, album);
    }

    @Bean
    public TradeService tradeService(TradeProposalRepository tradeProposalRepository,
                                     UserRepository userRepository,
                                     CollectionService collectionService,
                                     Album album,
                                     AlbumProperties albumProperties) {
        return new TradeService(
                tradeProposalRepository,
                userRepository,
                collectionService,
                album,
                albumProperties.getTrade().isProtectSingleSticker()
        );
    }

    @Bean
    public TradeSuggestionService tradeSuggestionService(UserRepository userRepository,
                                                         CollectionService collectionService,
                                                         AlbumProperties albumProperties) {
        return new TradeSuggestionService(
                userRepository,
                collectionService,
                albumProperties.getTrade().isProtectSingleSticker()
        );
    }

    @Bean
    public RankingService rankingService(UserRepository userRepository,
                                         CollectionService collectionService,
                                         TradeProposalRepository tradeProposalRepository,
                                         Album album) {
        return new RankingService(userRepository, collectionService, tradeProposalRepository, album);
    }
}
