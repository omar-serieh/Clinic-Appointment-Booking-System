// Developed by Omar Abou Serieh - 2025
package com.spring.clinic.controller;
import com.spring.clinic.dto.notifications.NotificationCreateDTO;
import com.spring.clinic.dto.notifications.NotificationsDTO;
import com.spring.clinic.dto.notifications.ReferenceRequestDTO;
import com.spring.clinic.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin-notification")
public class AdminNotificationController {
    private final NotificationService notificationService;

    public AdminNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    @GetMapping("/{id}")
    public ResponseEntity<NotificationsDTO> getNotificationById(@PathVariable long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }
    @GetMapping("/all")
    public ResponseEntity<List<NotificationsDTO>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }
    @PostMapping("/create-all")
    public ResponseEntity<?> createNotificationsForAllUsers(@RequestBody NotificationCreateDTO notificationsDTO) {
        notificationService.createNotificationsForAllUsers(notificationsDTO);
        return ResponseEntity.ok("The notifications were created successfully");
    }
    @PostMapping("/create/{userId}")
    public ResponseEntity<NotificationsDTO> createNotification(@RequestBody NotificationCreateDTO notificationsDTO, @PathVariable long userId) {
        return ResponseEntity.ok(notificationService.createNotification(notificationsDTO,userId));
    }
    @DeleteMapping("/delete-all-by")
    public ResponseEntity<?> deleteAllNotifications(@RequestBody ReferenceRequestDTO referenceRequestDTO) {
        notificationService.deleteNotificationByReferenceId(referenceRequestDTO.getReferenceId());
        return ResponseEntity.ok("All notifications were deleted successfully");
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable long id) {
        notificationService.deleteNotificationById(id);
        return ResponseEntity.ok("notification was deleted successfully");
    }


}
