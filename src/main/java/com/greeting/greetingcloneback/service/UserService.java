package com.greeting.greetingcloneback.service;

import com.greeting.greetingcloneback.dto.UserRegisterFormDTO;
import com.greeting.greetingcloneback.model.User;
import com.greeting.greetingcloneback.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public User login(UserRegisterFormDTO userInfo) {
        Optional<User> user = userRepository.findByEmail(userInfo.getEmail().toLowerCase());
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User does not exist");
        }

        if (passwordEncoder.matches(userInfo.getPassword(), user.get().getPassword())) {
            return user.get();
        }
        throw new BadCredentialsException("Wrong password");
    }

    public User create(UserRegisterFormDTO userInfo) {
        if (userRepository.findByEmail(userInfo.getEmail()).isPresent()) {
            throw new EntityExistsException("User with that email exists");
        }

        User user = new User();
        user.setEmail(userInfo.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        user.setFirstname(userInfo.getFirstname());
        user.setLastname(userInfo.getLastname());
        mailService.sendSignupMail(userInfo.getEmail(), userInfo.getFirstname(), userInfo.getLastname());
        userRepository.save(user);
        return user;
    }
}
