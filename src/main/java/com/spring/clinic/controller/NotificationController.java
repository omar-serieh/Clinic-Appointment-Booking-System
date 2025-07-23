package com.spring.clinic.controller;

import com.spring.clinic.dto.notifications.NotificationsDTO;
import com.spring.clinic.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    @GetMapping("/my")
    public ResponseEntity<List<NotificationsDTO>> getMyNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        List<NotificationsDTO> notifications=notificationService.getNotificationsByUserId(userDetails);
        return ResponseEntity.ok(notifications);
    }
    @GetMapping("/my-unread")
    public ResponseEntity<List<NotificationsDTO>> getMyUnReadNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        List<NotificationsDTO> notifications=notificationService.getMyUnReadUnreadNotificationsBy(userDetails);
        return ResponseEntity.ok(notifications);
    }
    @GetMapping("/my/{id}")
    public ResponseEntity<NotificationsDTO> getMyNotificationById(@AuthenticationPrincipal UserDetails userDetails,@PathVariable("id") int id) {
        return ResponseEntity.ok(notificationService.getMyNotificationsById(userDetails,id));
    }
    @PostMapping("/mark-read/{id}")
    public ResponseEntity<NotificationsDTO> markNotificationRead(@AuthenticationPrincipal UserDetails userDetails,@PathVariable("id") int id) {
        return ResponseEntity.ok(notificationService.markAsRead(userDetails,id));
    }
    @PostMapping("/mark-all-read")
    public ResponseEntity<?> markAllReadNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        notificationService.markAllAsReadByUserId(userDetails);
        return ResponseEntity.ok("All notifications marked as Read");
    }



    }
