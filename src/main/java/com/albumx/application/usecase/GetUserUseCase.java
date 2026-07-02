package com.albumx.application.usecase;

import com.albumx.application.dto.UserResponse;
import com.albumx.domain.model.User;
import com.albumx.domain.service.UserService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GetUserUseCase {

    private final UserService userService;

    public GetUserUseCase(UserService userService) {
        this.userService = userService;
    }

    public UserResponse execute(UUID id) {
        User user = userService.findById(id);
        return new UserResponse(user.getId(), user.getName());
    }
}
