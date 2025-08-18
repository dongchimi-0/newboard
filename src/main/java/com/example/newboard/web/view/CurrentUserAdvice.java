package com.example.newboard.web.view;

import com.example.newboard.domain.User;
import com.example.newboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class CurrentUserAdvice {
    private final UserRepository userRepository;

    @ModelAttribute("displayName")
    public String displayName(Authentication auth) {
        if (auth == null) return null;
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .map(User::getName)
                .filter(n -> !n.isBlank())
                .orElse(email); //
    }
}

