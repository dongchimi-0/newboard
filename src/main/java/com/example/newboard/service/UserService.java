package com.example.newboard.service;

import com.example.newboard.domain.User;
import com.example.newboard.repository.UserRepository;
import com.example.newboard.service.security.CustomUserDetails;
import com.example.newboard.web.dto.JoinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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

    @Transactional
    public String saveProfileImage(MultipartFile file, String email) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        User user;

        try {
            //  저장 폴더 확인 및 생성
            Path uploadDir = Paths.get("uploads/profile");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 고유한 파일명 생성
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = uploadDir.resolve(fileName);

            // 파일 저장
            Files.copy(file.getInputStream(), path);

            //  DB 업데이트
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            user.setProfileImageUrl("/uploads/profile/" + fileName);
            userRepository.save(user);

        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 저장 실패", e);
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(user),
                user.getPassword(),
                List.of(() -> "ROLE_" + user.getRole())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        return user.getProfileImageUrl();
    }


}
