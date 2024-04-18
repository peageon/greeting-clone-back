package com.greeting.greetingcloneback.service;

import com.greeting.greetingcloneback.dto.UserLoginDetail;
import com.greeting.greetingcloneback.entity.UserEntity;
import com.greeting.greetingcloneback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(email)
                .map(UserLoginDetail::new)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));
    }
}
