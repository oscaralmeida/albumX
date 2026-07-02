package com.albumx.domain.service;

import com.albumx.domain.exception.InvalidStickerNumberException;
import com.albumx.domain.exception.StickerNotOwnedException;
import com.albumx.domain.exception.UserNotFoundException;
import com.albumx.domain.model.Album;
import com.albumx.domain.model.CollectionEntry;
import com.albumx.domain.model.UserCollection;
import com.albumx.domain.repository.CollectionRepository;
import com.albumx.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollectionServiceTest {

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private UserRepository userRepository;

    private CollectionService collectionService;
    private final UUID userId = UUID.randomUUID();
    private final Album album = new Album(700);

    @BeforeEach
    void setUp() {
        collectionService = new CollectionService(collectionRepository, userRepository, album);
    }

    @Test
    void shouldAddStickerAndIncrementQuantity() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(collectionRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(collectionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CollectionEntry first = collectionService.addSticker(userId, 10);
        assertThat(first.getQuantity()).isEqualTo(1);

        UserCollection existing = new UserCollection(userId);
        existing.addSticker(10);
        when(collectionRepository.findByUserId(userId)).thenReturn(Optional.of(existing));

        CollectionEntry second = collectionService.addSticker(userId, 10);
        assertThat(second.getQuantity()).isEqualTo(2);
    }

    @Test
    void shouldRejectInvalidStickerNumber() {
        when(userRepository.existsById(userId)).thenReturn(true);

        assertThatThrownBy(() -> collectionService.addSticker(userId, 0))
                .isInstanceOf(InvalidStickerNumberException.class);
        assertThatThrownBy(() -> collectionService.addSticker(userId, 701))
                .isInstanceOf(InvalidStickerNumberException.class);
    }

    @Test
    void shouldRejectMissingUser() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> collectionService.addSticker(userId, 10))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldReturnOnlyDuplicatesWithQuantityGreaterThanOne() {
        when(userRepository.existsById(userId)).thenReturn(true);
        UserCollection collection = new UserCollection(userId);
        collection.addSticker(10);
        collection.addSticker(10);
        collection.addSticker(25);
        when(collectionRepository.findByUserId(userId)).thenReturn(Optional.of(collection));

        List<CollectionEntry> duplicates = collectionService.getDuplicates(userId);

        assertThat(duplicates).hasSize(1);
        assertThat(duplicates.get(0).getStickerNumber()).isEqualTo(10);
        assertThat(duplicates.get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    void shouldReturnEmptyDuplicatesWhenNone() {
        when(userRepository.existsById(userId)).thenReturn(true);
        UserCollection collection = new UserCollection(userId);
        collection.addSticker(5);
        when(collectionRepository.findByUserId(userId)).thenReturn(Optional.of(collection));

        List<CollectionEntry> duplicates = collectionService.getDuplicates(userId);

        assertThat(duplicates).isEmpty();
    }

    @Test
    void shouldRemoveStickerAndDecrementQuantity() {
        when(userRepository.existsById(userId)).thenReturn(true);
        UserCollection collection = new UserCollection(userId);
        collection.addSticker(10);
        collection.addSticker(10);
        when(collectionRepository.findByUserId(userId)).thenReturn(Optional.of(collection));
        when(collectionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CollectionEntry result = collectionService.removeSticker(userId, 10);

        assertThat(result.getQuantity()).isEqualTo(1);
    }

    @Test
    void shouldRemoveEntryWhenQuantityReachesZero() {
        when(userRepository.existsById(userId)).thenReturn(true);
        UserCollection collection = new UserCollection(userId);
        collection.addSticker(10);
        when(collectionRepository.findByUserId(userId)).thenReturn(Optional.of(collection));
        when(collectionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CollectionEntry result = collectionService.removeSticker(userId, 10);

        assertThat(result.getQuantity()).isEqualTo(0);
    }

    @Test
    void shouldRejectRemoveWhenQuantityIsZero() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(collectionRepository.findByUserId(userId)).thenReturn(Optional.of(new UserCollection(userId)));

        assertThatThrownBy(() -> collectionService.removeSticker(userId, 10))
                .isInstanceOf(StickerNotOwnedException.class);
    }

    @Test
    void shouldReturnQuantityForSticker() {
        when(userRepository.existsById(userId)).thenReturn(true);
        UserCollection collection = new UserCollection(userId);
        collection.addSticker(10);
        collection.addSticker(10);
        when(collectionRepository.findByUserId(userId)).thenReturn(Optional.of(collection));

        assertThat(collectionService.getQuantity(userId, 10)).isEqualTo(2);
        assertThat(collectionService.getQuantity(userId, 99)).isEqualTo(0);
    }

    @Test
    void shouldDetectDuplicateWhenQuantityGreaterThanOne() {
        when(userRepository.existsById(userId)).thenReturn(true);
        UserCollection collection = new UserCollection(userId);
        collection.addSticker(10);
        collection.addSticker(10);
        when(collectionRepository.findByUserId(userId)).thenReturn(Optional.of(collection));

        assertThat(collectionService.hasDuplicate(userId, 10)).isTrue();
        assertThat(collectionService.hasDuplicate(userId, 25)).isFalse();
    }

    @Test
    void shouldReturnUniqueStickerCount() {
        when(userRepository.existsById(userId)).thenReturn(true);
        UserCollection collection = new UserCollection(userId);
        collection.addSticker(10);
        collection.addSticker(10);
        collection.addSticker(25);
        when(collectionRepository.findByUserId(userId)).thenReturn(Optional.of(collection));

        assertThat(collectionService.getUniqueStickerCount(userId)).isEqualTo(2);
    }

    @Test
    void shouldReturnZeroUniqueStickerCountForEmptyCollection() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(collectionRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThat(collectionService.getUniqueStickerCount(userId)).isEqualTo(0);
    }

    @Test
    void shouldReturnMissingStickerNumbers() {
        when(userRepository.existsById(userId)).thenReturn(true);
        UserCollection collection = new UserCollection(userId);
        collection.addSticker(1);
        collection.addSticker(3);
        when(collectionRepository.findByUserId(userId)).thenReturn(Optional.of(collection));

        List<Integer> missing = collectionService.getMissingStickerNumbers(userId);

        assertThat(missing).contains(2);
        assertThat(missing).doesNotContain(1, 3);
        assertThat(missing).hasSize(698);
    }

    @Test
    void shouldAllowOfferWhenProtectionDisabledAndQuantityIsOne() {
        when(userRepository.existsById(userId)).thenReturn(true);
        UserCollection collection = new UserCollection(userId);
        collection.addSticker(10);
        when(collectionRepository.findByUserId(userId)).thenReturn(Optional.of(collection));

        assertThat(collectionService.canOfferSticker(userId, 10, false)).isTrue();
    }

    @Test
    void shouldRejectOfferWhenProtectionEnabledAndQuantityIsOne() {
        when(userRepository.existsById(userId)).thenReturn(true);
        UserCollection collection = new UserCollection(userId);
        collection.addSticker(10);
        when(collectionRepository.findByUserId(userId)).thenReturn(Optional.of(collection));

        assertThat(collectionService.canOfferSticker(userId, 10, true)).isFalse();
    }

    @Test
    void shouldAllowOfferWhenProtectionEnabledAndQuantityIsTwoOrMore() {
        when(userRepository.existsById(userId)).thenReturn(true);
        UserCollection collection = new UserCollection(userId);
        collection.addSticker(10);
        collection.addSticker(10);
        when(collectionRepository.findByUserId(userId)).thenReturn(Optional.of(collection));

        assertThat(collectionService.canOfferSticker(userId, 10, true)).isTrue();
    }
}
