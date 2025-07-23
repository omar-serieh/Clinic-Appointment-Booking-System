package com.spring.clinic.dto.notifications;

import lombok.Data;

@Data
public class NotificationCreateDTO {
    private String title;
    private String message;
    private String notificationType;
    private boolean isGlobal;
    private String referenceId;
    public NotificationCreateDTO(String title, String message, String notificationType) {
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
    }

}
