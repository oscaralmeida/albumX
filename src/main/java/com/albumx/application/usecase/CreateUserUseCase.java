package com.albumx.application.usecase;

import com.albumx.application.dto.CreateUserRequest;
import com.albumx.application.dto.UserResponse;
import com.albumx.domain.model.User;
import com.albumx.domain.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class CreateUserUseCase {

    private final UserService userService;

    public CreateUserUseCase(UserService userService) {
        this.userService = userService;
    }

    public UserResponse execute(CreateUserRequest request) {
        User user = userService.register(request.name());
        return new UserResponse(user.getId(), user.getName());
    }
}
