package com.example.newboard.service.security;

import com.example.newboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var u = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User  // 스프링 시큐리티에서 제공하는 UserDetails 구현체
                .withUsername(u.getEmail())
                .password(u.getPassword())
                .roles(u.getRole()) // "USER"
                .build();
    }   // Authentication 객체로 저장
}

// 로그인 시도 -> 스프링이 낚아챔 -> (이클래스에서) 일단 유저엔티티로 담아온다음 UserDetails 형태로 옮겨 담아서 가져감.
// 거기서 또 꺼내서 결국은 Authentication 객체로 저장  // 결론: 로그인은 스프링이 관리하겠다.

