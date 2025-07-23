package com.spring.clinic.service;

import com.spring.clinic.dto.notifications.NotificationCreateDTO;
import com.spring.clinic.dto.notifications.NotificationsDTO;
import com.spring.clinic.entity.Notifications;
import com.spring.clinic.entity.Users;
import com.spring.clinic.exception.ApiException;
import com.spring.clinic.repository.NotificationsRepository;
import com.spring.clinic.repository.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final UsersRepository usersRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationsRepository notificationsRepository;
    private final UsersService usersService;

    public NotificationServiceImpl(UsersRepository usersRepository, SimpMessagingTemplate messagingTemplate, NotificationsRepository notificationsRepository, UsersService usersService) {
        this.usersRepository = usersRepository;
        this.messagingTemplate = messagingTemplate;
        this.notificationsRepository = notificationsRepository;
        this.usersService = usersService;
    }

    @Override
    public NotificationsDTO getNotificationById(long notificationId) {
        Optional<Notifications> notification=notificationsRepository.findById(notificationId);
        if(notification.isEmpty()){
            throw new ApiException("Notification not found", HttpStatus.NOT_FOUND);
        }
        Notifications notifications=notification.get();
        return getNotificationsDTO(notifications,notifications.getUser());
    }

    @Override
    public List<NotificationsDTO> getAllNotifications() {
        List<Notifications> notifications = notificationsRepository.findAll();
        if(notifications.isEmpty()){
            throw new ApiException("Notifications not found", HttpStatus.NOT_FOUND);
        }
        return notifications.stream().map(notification -> getNotificationsDTO(notification,notification.getUser())).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createNotificationsForAllUsers(NotificationCreateDTO notificationCreateDTO) {
        if (notificationCreateDTO.isGlobal()) {
            String referenceId = UUID.randomUUID().toString();
            List<Users> allUsers = usersRepository.findAll();
            for (Users user : allUsers) {
                Notifications notification = new Notifications();
                notification.setUser(user);
                notification.setTitle(notificationCreateDTO.getTitle());
                notification.setMessage(notificationCreateDTO.getMessage());
                notification.setRead(false);
                notification.setNotificationType(Notifications.NotificationType.valueOf(notificationCreateDTO.getNotificationType()));
                notification.setReferenceId(referenceId);
                notificationsRepository.save(notification);
                NotificationsDTO notificationsDTO = getNotificationsDTO(notification, user);
                messagingTemplate.convertAndSendToUser(
                        user.getEmail(),
                        "/queue/notifications",
                        notificationsDTO
                );
            }
        } else {
            throw new ApiException("This method is for global notifications only.", HttpStatus.METHOD_NOT_ALLOWED);
        }
    }


    @Override
    @Transactional
    public NotificationsDTO createNotification(NotificationCreateDTO notificationCreateDTO,long userId) {
        Optional<Users> user=usersRepository.findById(userId);
        if(user.isEmpty()){
            throw new ApiException("User not found", HttpStatus.NOT_FOUND);
        }
        Users users=user.get();
        Notifications notifications=new Notifications();
        notifications.setUser(users);
        notifications.setTitle(notificationCreateDTO.getTitle());
        notifications.setMessage(notificationCreateDTO.getMessage());
        notifications.setRead(false);
        notifications.setNotificationType(Notifications.NotificationType.valueOf(notificationCreateDTO.getNotificationType()));
        notificationsRepository.save(notifications);
        notifications.setReferenceId(String.valueOf(notifications.getId()));
        notificationsRepository.save(notifications);
        NotificationsDTO notificationsDTO = getNotificationsDTO(notifications, users);
        messagingTemplate.convertAndSendToUser(
                        users.getEmail(),
                "/queue/notifications",
                notificationsDTO);
        return notificationsDTO;
    }
    @Override
    public List<NotificationsDTO> getNotificationsByUserId(@AuthenticationPrincipal UserDetails userDetails) {
        Users users=usersService.fetchUserByToken(userDetails);
        List<Notifications> notifications = notificationsRepository.findByUser(users);
        if(notifications.isEmpty()){
            throw new ApiException("Notifications not found", HttpStatus.NOT_FOUND);
        }
        return notifications.stream().map(notification -> getNotificationsDTO(notification,users)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<NotificationsDTO> getMyUnReadUnreadNotificationsBy(@AuthenticationPrincipal UserDetails userDetails) {
       Users users=usersService.fetchUserByToken(userDetails);
        List<Notifications> notifications = notificationsRepository.findByUserAndReadIsFalse(users);
        if(notifications.isEmpty()){
            throw new ApiException("No unread notifications", HttpStatus.NOT_FOUND);
        }
        return notifications.stream().map(notification -> getNotificationsDTO(notification,users)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationsDTO markAsRead(@AuthenticationPrincipal UserDetails userDetails,long notificationId) {
        Users users=usersService.fetchUserByToken(userDetails);
        Optional<Notifications> notification=notificationsRepository.findByIdAndUser(notificationId,users);
        if(notification.isEmpty()){
            throw new ApiException("Notification not found", HttpStatus.NOT_FOUND);
        }
        Notifications notifications=notification.get();
        notifications.setRead(true);
        notificationsRepository.save(notifications);
        return getNotificationsDTO(notifications,users);
    }

    @Override
    @Transactional
    public void markAllAsReadByUserId(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Users users = usersService.fetchUserByToken(userDetails);
            List<Notifications> notifications = notificationsRepository.findByUserAndReadIsFalse(users);
            if(notifications.isEmpty()){
                throw new ApiException("No unread notifications", HttpStatus.NOT_FOUND);
            }
            for (Notifications notification : notifications) {
                notification.setRead(true);
                notificationsRepository.save(notification);
            }
        }catch(Exception e){
            throw new ApiException("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public NotificationsDTO getMyNotificationsById(@AuthenticationPrincipal UserDetails userDetails, long notificationId) {
        Users users = usersService.fetchUserByToken(userDetails);
        Optional<Notifications> notification=notificationsRepository.findByIdAndUser(notificationId,users);
        if(notification.isEmpty()){
            throw new ApiException("Notification not found", HttpStatus.NOT_FOUND);
        }
        Notifications notifications=notification.get();
        return getNotificationsDTO(notifications,users);
    }

    @Override
    public void deleteNotificationByReferenceId(String ReferenceId) {
        List<Notifications> notifications=notificationsRepository.findByReferenceId(ReferenceId);
        if(notifications.isEmpty()){
            throw new ApiException("Notification not found", HttpStatus.NOT_FOUND);
        }
        notificationsRepository.deleteAll(notifications);
    }

    @Override
    public void deleteNotificationById(long id) {
        notificationsRepository.deleteById(id);
    }

    private static NotificationsDTO getNotificationsDTO(Notifications notifications, Users users) {
        NotificationsDTO notificationsDTO=new NotificationsDTO();
        notificationsDTO.setId(notifications.getId());
        notificationsDTO.setTitle(notifications.getTitle());
        notificationsDTO.setMessage(notifications.getMessage());
        notificationsDTO.setRead(notifications.isRead());
        notificationsDTO.setNotificationType(String.valueOf(notifications.getNotificationType()));
        notificationsDTO.setReferenceId(notifications.getReferenceId());
        notificationsDTO.setCreatedAt(notifications.getCreatedAt());
        NotificationsDTO.UserInfo userInfo=new NotificationsDTO.UserInfo();
        userInfo.setEmail(users.getEmail());
        userInfo.setId(users.getUserId());
        userInfo.setUsername(users.getName());
        userInfo.setPhone(users.getPhoneNumber());
        notificationsDTO.setUserInfo(userInfo);
        return notificationsDTO;
    }
}
