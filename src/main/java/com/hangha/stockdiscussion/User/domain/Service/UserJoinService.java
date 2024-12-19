package com.hangha.stockdiscussion.User.domain.Service;

import com.hangha.stockdiscussion.User.application.command.RegisterUserCommand;
import com.hangha.stockdiscussion.User.domain.entity.User;
import com.hangha.stockdiscussion.User.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserJoinService implements UserService {

    private final UserRepository userRepository;
    private final UserDomainService userDomainService;

    public UserJoinService(UserRepository userRepository, UserDomainService userDomainService) {
        this.userRepository = userRepository;
        this.userDomainService = userDomainService;
    }

    @Override
    public User registerUser(RegisterUserCommand command) {
        String encodedPassword = userDomainService.encodePassword(command.password());
        User user = new User(
                null,
                command.username(),
                encodedPassword,
                command.email(),
                command.intro(),
                command.imageUrl(),
                LocalDateTime.now()

        );
        return userRepository.save(user);
    }

    @Override
    public User findUserByUsername(String username) {
        return null;
    }
}
