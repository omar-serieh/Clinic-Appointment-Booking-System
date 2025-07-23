package com.spring.clinic.dto.notifications;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationsDTO {
    private long id;
    private String title;
    private String message;
    private boolean isRead;
    private String notificationType;
    private UserInfo userInfo;
    private String referenceId;
    private LocalDateTime createdAt;
    @Data
    public static class UserInfo{
        private long id;
        private String username;
        private String email;
        private String phone;
    }
}
