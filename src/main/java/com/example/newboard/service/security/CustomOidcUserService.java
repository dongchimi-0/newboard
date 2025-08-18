package com.example.newboard.service.security;

import com.example.newboard.domain.User;
import com.example.newboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest req) throws OAuth2AuthenticationException {
        OidcUserService delegate = new OidcUserService();
        OidcUser u = delegate.loadUser(req);

        String email = u.getEmail();
        if (email == null || email.isBlank()) {
            email = u.<String>getAttribute("email");
        }
        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException("Google(OIDC)에서 email을 가져올 수 없습니다.");
        }

        final String finalEmail = email;
        final String finalName  = u.getFullName() != null ? u.getFullName() : u.<String>getAttribute("name");
        userRepository.findByEmail(finalEmail)
                .orElseGet(() -> userRepository.save(User.create(finalEmail, finalName)));

        return new DefaultOidcUser(
                new HashSet<>(u.getAuthorities()),
                u.getIdToken(),
                u.getUserInfo(),
                "email" // auth.getName()을 email로 고정
        );
    }
}

