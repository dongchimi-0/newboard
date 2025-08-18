package com.example.newboard.service;

import com.example.newboard.domain.User;
import com.example.newboard.repository.UserRepository;
import com.example.newboard.web.dto.JoinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(JoinRequest req){
        if (userRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        userRepository.save(User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .role("USER")
                .build());
    }
}

