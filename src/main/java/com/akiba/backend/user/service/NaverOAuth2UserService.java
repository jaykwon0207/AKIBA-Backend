package com.akiba.backend.user.service;

import com.akiba.backend.user.domain.AuthProvider;
import com.akiba.backend.user.domain.User;
import com.akiba.backend.user.domain.UserProfile;
import com.akiba.backend.user.repository.UserProfileRepository;
import com.akiba.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NaverOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 네이버는 response 안에 실제 데이터가 있음
        Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes().get("response");

        String oauthId = (String) response.get("id");
        String email = (String) response.get("email");
        String nickname = (String) response.get("nickname");

        // 기존 회원인지 확인
        User user = userRepository.findByProviderAndOauthId(AuthProvider.NAVER, oauthId)
                .orElseGet(() -> {
                    // 신규 회원이면 저장
                    User newUser = User.builder()
                            .email(email)
                            .provider(AuthProvider.NAVER)
                            .oauthId(oauthId)
                            .nickname(nickname)
                            .build();
                    User savedUser = userRepository.save(newUser);

                    // UserProfile도 같이 생성
                    UserProfile profile = UserProfile.builder()
                            .user(savedUser)
                            .build();
                    userProfileRepository.save(profile);

                    return savedUser;
                });

        return oAuth2User;
    }
}