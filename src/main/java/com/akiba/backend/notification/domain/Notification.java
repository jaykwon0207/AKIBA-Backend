package com.akiba.backend.notification.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications",
        indexes = {
                @Index(name = "idx_notifications_user_read_created",
                        columnList = "user_id, is_read, created_at")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationType type;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 300)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationLinkType linkType;

    private Long linkId; // 다형 참조 → FK 없음

    @Column(nullable = false)
    private boolean isRead;

    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    // 도메인 로직
    public void markAsRead() {
        this.isRead = true;
    }
}
