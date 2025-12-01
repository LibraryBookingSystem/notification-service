package com.library.notification_service.service;

import com.library.notification_service.dto.NotificationResponse;
import com.library.notification_service.entity.Notification;
import com.library.notification_service.entity.NotificationType;
import com.library.notification_service.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for notification operations
 */
@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    
    public NotificationService(NotificationRepository notificationRepository,
                              EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }
    
    /**
     * Create and send a notification
     */
    @Transactional
    public NotificationResponse createNotification(Long userId, NotificationType type,
                                                   String title, String message) {
        logger.info("Creating notification for user: {}, type: {}", userId, type);
        
        Notification notification = new Notification(userId, type, title, message);
        notification = notificationRepository.save(notification);
        
        // Send email asynchronously
        try {
            emailService.sendNotificationEmail(userId, title, message);
            notification.setEmailSent(true);
            notificationRepository.save(notification);
        } catch (Exception e) {
            logger.error("Failed to send email notification: {}", e.getMessage());
            // Continue even if email fails
        }
        
        logger.info("Notification created successfully: {} (ID: {})", type, notification.getId());
        return NotificationResponse.fromNotification(notification);
    }
    
    /**
     * Get notifications by user ID
     */
    public List<NotificationResponse> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId).stream()
            .map(NotificationResponse::fromNotification)
            .collect(Collectors.toList());
    }
    
    /**
     * Get unread notifications by user ID
     */
    public List<NotificationResponse> getUnreadNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId).stream()
            .map(NotificationResponse::fromNotification)
            .collect(Collectors.toList());
    }
    
    /**
     * Mark notification as read
     */
    @Transactional
    public NotificationResponse markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));
        
        notification.setIsRead(true);
        notification = notificationRepository.save(notification);
        
        return NotificationResponse.fromNotification(notification);
    }
    
    /**
     * Mark all notifications as read for a user
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        unreadNotifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }
    
    /**
     * Get unread count for a user
     */
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
}

