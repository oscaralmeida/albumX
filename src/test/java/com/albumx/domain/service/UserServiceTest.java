package com.albumx.domain.service;

import com.albumx.domain.exception.InvalidUserNameException;
import com.albumx.domain.model.User;
import com.albumx.domain.repository.CollectionRepository;
import com.albumx.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CollectionRepository collectionRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, collectionRepository);
    }

    @Test
    void shouldRegisterUserWithValidName() {
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User user = userService.register("Alice");

        assertThat(user.getId()).isNotNull();
        assertThat(user.getName()).isEqualTo("Alice");
        verify(collectionRepository).save(any());
    }

    @Test
    void shouldRejectEmptyName() {
        assertThatThrownBy(() -> userService.register("  "))
                .isInstanceOf(InvalidUserNameException.class);
    }

    @Test
    void shouldFindUserById() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "Bob");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User found = userService.findById(id);

        assertThat(found.getName()).isEqualTo("Bob");
    }
}
