package com.akiba.backend.profile.service;

import com.akiba.backend.config.exception.AlreadyFollowingException;
import com.akiba.backend.config.exception.SelfFollowException;
import com.akiba.backend.profile.dto.FollowResponse;
import com.akiba.backend.profile.dto.ProfileResponse;
import com.akiba.backend.user.domain.Follow;
import com.akiba.backend.user.domain.FollowId;
import com.akiba.backend.user.domain.User;
import com.akiba.backend.user.domain.UserProfile;
import com.akiba.backend.user.repository.FollowRepository;
import com.akiba.backend.user.repository.UserProfileRepository;
import com.akiba.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final FollowRepository followRepository;

    public ProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("프로필을 찾을 수 없습니다."));

        long followerCount = followRepository.countByFollowingId(userId);
        long followingCount = followRepository.countByFollowerId(userId);

        return ProfileResponse.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .bio(userProfile.getBio())
                .profileImageUrl(userProfile.getProfileImageUrl())
                .mannerScore(userProfile.getMannerScore())
                .ongoingDealCount(0)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .build();
    }
    public FollowResponse follow(Long followerId, Long targetId) {
        if (followerId.equals(targetId)) {
            throw new SelfFollowException("자기 자신을 팔로우할 수 없습니다.");
        }

        userRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        if (followRepository.existsById(new FollowId(followerId, targetId))) {
            throw new AlreadyFollowingException("이미 팔로우 중입니다.");
        }

        followRepository.save(Follow.builder()
                .followerId(followerId)
                .followingId(targetId)
                .build());

        return FollowResponse.builder()
                .message("팔로우 성공")
                .targetId(targetId)
                .build();
    }

    public FollowResponse unfollow(Long followerId, Long targetId) {
        FollowId followId = new FollowId(followerId, targetId);

        if (!followRepository.existsById(followId)) {
            throw new RuntimeException("팔로우 관계가 없습니다.");
        }

        followRepository.deleteById(followId);

        return FollowResponse.builder()
                .message("언팔로우 성공")
                .targetId(targetId)
                .build();
    }




}