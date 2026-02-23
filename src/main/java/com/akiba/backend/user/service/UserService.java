package com.akiba.backend.user.service;

import com.akiba.backend.config.jwt.TokenProvider;
import com.akiba.backend.user.domain.AuthProvider;
import com.akiba.backend.user.domain.RefreshToken;
import com.akiba.backend.user.domain.User;
import com.akiba.backend.user.domain.UserProfile;
import com.akiba.backend.user.dto.LoginRequest;
import com.akiba.backend.user.dto.LoginResponse;
import com.akiba.backend.user.dto.NicknameRequest;
import com.akiba.backend.user.dto.NicknameResponse;
import com.akiba.backend.user.repository.RefreshTokenRepository;
import com.akiba.backend.user.repository.UserProfileRepository;
import com.akiba.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 1. 인가 코드로 네이버에 액세스 토큰 요청
        String naverAccessToken = getNaverAccessToken(request.getCode(), request.getState());

        // 2. 액세스 토큰으로 유저 정보 요청
        Map<String, Object> userInfo = getNaverUserInfo(naverAccessToken);
        Map<String, Object> response = (Map<String, Object>) userInfo.get("response");

        String oauthId = (String) response.get("id");
        String email = (String) response.get("email");
        String nickname = (String) response.get("nickname");

        // 3. 신규 유저인지 확인
        boolean isNewUser = !userRepository.findByProviderAndOauthId(AuthProvider.NAVER, oauthId).isPresent();

        User user = userRepository.findByProviderAndOauthId(AuthProvider.NAVER, oauthId)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .provider(AuthProvider.NAVER)
                            .oauthId(oauthId)
                            .nickname(nickname)
                            .build();
                    User savedUser = userRepository.save(newUser);

                    UserProfile profile = UserProfile.builder()
                            .user(savedUser)
                            .build();
                    userProfileRepository.save(profile);

                    return savedUser;
                });

        // 4. JWT 토큰 발급
        String accessToken = tokenProvider.generateAccessToken(user.getUserId());
        String refreshToken = tokenProvider.generateRefreshToken(user.getUserId());

        // 5. refreshToken DB 저장 (있으면 업데이트, 없으면 새로 저장)
        refreshTokenRepository.findByUserId(user.getUserId())
                .ifPresentOrElse(
                        token -> token.update(refreshToken),
                        () -> refreshTokenRepository.save(new RefreshToken(user.getUserId(), refreshToken))
                );

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .isNewUser(isNewUser)
                .build();


    }

    private String getNaverAccessToken(String code, String state) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://nid.naver.com/oauth2.0/token"
                + "?grant_type=authorization_code"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&code=" + code
                + "&state=" + state;

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        return (String) response.getBody().get("access_token");
    }

    private Map<String, Object> getNaverUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                entity,
                Map.class
        );
        return response.getBody();
    }

    @Transactional
    public NicknameResponse updateNickname(Long userId, NicknameRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        user.updateNickname(request.getNickname());

        return NicknameResponse.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .build();
    }

    //닉네임 중복 체크
    public boolean checkNickname(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }
}