package com.albumx.domain.service;

import com.albumx.domain.exception.UserNotFoundException;
import com.albumx.domain.model.TradeSuggestion;
import com.albumx.domain.model.User;
import com.albumx.domain.model.UserCollection;
import com.albumx.domain.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TradeSuggestionService {

    private final UserRepository userRepository;
    private final CollectionService collectionService;
    private final boolean protectSingleSticker;

    public TradeSuggestionService(UserRepository userRepository,
                                  CollectionService collectionService,
                                  boolean protectSingleSticker) {
        this.userRepository = userRepository;
        this.collectionService = collectionService;
        this.protectSingleSticker = protectSingleSticker;
    }

    public List<TradeSuggestion> findSuggestionsForUser(UUID userAId) {
        ensureUserExists(userAId);
        List<TradeSuggestion> suggestions = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (User partner : userRepository.findAll()) {
            if (partner.getId().equals(userAId)) {
                continue;
            }
            addSuggestionsBetween(userAId, partner.getId(), suggestions, seen);
        }

        return suggestions;
    }

    public List<TradeSuggestion> findAllSuggestions() {
        List<User> allUsers = userRepository.findAll();
        List<TradeSuggestion> suggestions = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (int i = 0; i < allUsers.size(); i++) {
            for (int j = i + 1; j < allUsers.size(); j++) {
                User first = allUsers.get(i);
                User second = allUsers.get(j);
                UUID requesterId = first.getId().compareTo(second.getId()) < 0
                        ? first.getId()
                        : second.getId();
                UUID partnerId = requesterId.equals(first.getId()) ? second.getId() : first.getId();
                addSuggestionsBetween(requesterId, partnerId, suggestions, seen);
            }
        }

        return suggestions;
    }

    private void addSuggestionsBetween(UUID userAId,
                                       UUID userBId,
                                       List<TradeSuggestion> suggestions,
                                       Set<String> seen) {
        List<Integer> offerableByA = offerableBy(userAId, userBId);
        List<Integer> offerableByB = offerableBy(userBId, userAId);

        for (int stickerX : offerableByA) {
            for (int stickerY : offerableByB) {
                if (stickerX == stickerY) {
                    continue;
                }
                String key = userAId + "|" + userBId + "|" + stickerX + "|" + stickerY;
                if (!seen.add(key)) {
                    continue;
                }
                String reason = String.format(
                        "Troca mútua: você oferece #%d, parceiro oferece #%d",
                        stickerX,
                        stickerY
                );
                suggestions.add(new TradeSuggestion(userAId, userBId, stickerX, stickerY, reason));
            }
        }
    }

    private List<Integer> offerableBy(UUID ownerId, UUID partnerId) {
        UserCollection ownerCollection = collectionService.getCollectionReadOnly(ownerId);
        UserCollection partnerCollection = collectionService.getCollectionReadOnly(partnerId);
        List<Integer> offerable = new ArrayList<>();

        for (int stickerNumber : ownerCollection.getUniqueStickerNumbers()) {
            if (collectionService.canOfferSticker(ownerId, stickerNumber, protectSingleSticker)
                    && partnerCollection.getQuantity(stickerNumber) == 0) {
                offerable.add(stickerNumber);
            }
        }

        return offerable;
    }

    private void ensureUserExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }
}
