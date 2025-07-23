package com.spring.clinic.service;

import com.spring.clinic.dto.notifications.NotificationCreateDTO;
import com.spring.clinic.dto.notifications.NotificationsDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface NotificationService {
    NotificationsDTO getNotificationById(long id);
    List<NotificationsDTO> getAllNotifications();
    void createNotificationsForAllUsers(NotificationCreateDTO notificationCreateDTO);
    NotificationsDTO createNotification(NotificationCreateDTO notification,long userId);
    List<NotificationsDTO> getNotificationsByUserId(@AuthenticationPrincipal UserDetails userDetails);
    List<NotificationsDTO> getMyUnReadUnreadNotificationsBy(@AuthenticationPrincipal UserDetails userDetails);
    NotificationsDTO markAsRead(@AuthenticationPrincipal UserDetails userDetails,long notificationId);
    void markAllAsReadByUserId(@AuthenticationPrincipal UserDetails userDetails);
    NotificationsDTO getMyNotificationsById(@AuthenticationPrincipal UserDetails userDetails,long notificationId);
    void deleteNotificationByReferenceId(String ReferenceId);
    void deleteNotificationById(long id);
}
