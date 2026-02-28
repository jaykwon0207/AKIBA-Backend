package com.akiba.backend.user.service;

import com.akiba.backend.user.domain.UserRecentView;
import com.akiba.backend.user.repository.UserRecentViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRecentViewService {

    private final UserRecentViewRepository userRecentViewRepository;

    @Transactional
    public void touchRecentView(Long userId, Long postId) {
        if (userId == null || postId == null) return;
        UserRecentView recentView = userRecentViewRepository.findByUserIdAndPostId(userId, postId)
                .orElseGet(() -> UserRecentView.builder()
                        .userId(userId)
                        .postId(postId)
                        .build());
        recentView.touch();
        userRecentViewRepository.save(recentView);
    }

    public List<Long> findRecentPostIds(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("viewedAt").descending());
        return userRecentViewRepository.findByUserIdOrderByViewedAtDesc(userId, pageable)
                .getContent()
                .stream()
                .map(UserRecentView::getPostId)
                .toList();
    }
}
