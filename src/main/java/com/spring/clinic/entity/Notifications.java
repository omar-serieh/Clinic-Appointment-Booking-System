package com.spring.clinic.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Users user;

    private String title;

    private String message;
    @Column(name = "is_read")
    private boolean isRead;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private String referenceId;


    public enum NotificationType{
        INFO,
        ALERT,
        CONFIRM
    }

}
