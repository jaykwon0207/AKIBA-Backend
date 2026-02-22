package com.akiba.backend.config;

import com.akiba.backend.config.jwt.TokenProvider;
import com.akiba.backend.user.domain.User;
import com.akiba.backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> naverResponse = (Map<String, Object>) oAuth2User.getAttributes().get("response");

        String oauthId = (String) naverResponse.get("id");

        User user = userRepository.findByProviderAndOauthId(
                        com.akiba.backend.user.domain.AuthProvider.NAVER, oauthId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        String accessToken = tokenProvider.generateAccessToken(user.getUserId());
        String refreshToken = tokenProvider.generateRefreshToken(user.getUserId());

        // 토큰을 쿼리파라미터로 프론트에 전달
        String redirectUrl = "http://localhost:3000/oauth/callback"
                + "?accessToken=" + accessToken
                + "&refreshToken=" + refreshToken;

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}