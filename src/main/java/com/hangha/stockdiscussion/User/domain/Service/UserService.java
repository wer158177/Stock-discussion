package com.hangha.stockdiscussion.User.domain.Service;

import com.hangha.stockdiscussion.User.application.command.RegisterUserCommand;
import com.hangha.stockdiscussion.User.domain.entity.User;

public interface UserService {
    User registerUser(RegisterUserCommand command);
    User findUserByUsername(String username);
}