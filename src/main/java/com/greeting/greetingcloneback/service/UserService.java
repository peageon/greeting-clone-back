package com.greeting.greetingcloneback.service;

import com.greeting.greetingcloneback.dto.UserRegisterFormDTO;
import com.greeting.greetingcloneback.entity.UserEntity;
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

    public UserEntity login(UserRegisterFormDTO userInfo) {
        Optional<UserEntity> user = userRepository.findByEmail(userInfo.getEmail().toLowerCase());
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User does not exist");
        }

        if (passwordEncoder.matches(userInfo.getPassword(), user.get().getPassword())) {
            return user.get();
        }
        throw new BadCredentialsException("Wrong password");
    }

    public UserEntity create(UserRegisterFormDTO userInfo) {
        if (userRepository.findByEmail(userInfo.getEmail()).isPresent()) {
            throw new EntityExistsException("User with that email exists");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userInfo.getEmail().toLowerCase());
        userEntity.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        userEntity.setFirstname(userInfo.getFirstname());
        userEntity.setLastname(userInfo.getLastname());
//        mailService.sendSignupMail(userInfo.getEmail(), userInfo.getFirstname(), userInfo.getLastname());
        userRepository.save(userEntity);
        return userEntity;
    }
}
