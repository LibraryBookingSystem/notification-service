package com.library.notification_service.service;

import com.library.notification_service.dto.NotificationResponse;
import com.library.notification_service.entity.Notification;
import com.library.notification_service.entity.NotificationType;
import com.library.notification_service.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for notification operations
 */
@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final RestTemplate restTemplate;
    
    @Value("${user-service-url}")
    private String userServiceUrl;
    
    public NotificationService(NotificationRepository notificationRepository,
                              EmailService emailService,
                              RestTemplate restTemplate) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
        this.restTemplate = restTemplate;
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
    
    /**
     * Create notifications for all users
     * Used for system-wide announcements (e.g., new resources, policy changes)
     */
    @Transactional
    public void createNotificationForAllUsers(NotificationType type, String title, String message) {
        logger.info("Creating notification for all users: type={}, title={}", type, title);
        
        try {
            // Fetch all users from user service
            List<Map<String, Object>> users = restTemplate.exchange(
                userServiceUrl + "/api/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            ).getBody();
            
            if (users == null || users.isEmpty()) {
                logger.warn("No users found to notify");
                return;
            }
            
            int successCount = 0;
            int failureCount = 0;
            
            // Create notification for each user
            for (Map<String, Object> user : users) {
                try {
                    Long userId = Long.valueOf(user.get("id").toString());
                    
                    // Skip admin users for resource/policy notifications (they already know)
                    String role = user.get("role") != null ? user.get("role").toString() : "";
                    if ("ADMIN".equals(role) && (type == NotificationType.RESOURCE_CREATED || 
                                                  type == NotificationType.RESOURCE_DELETED ||
                                                  type == NotificationType.POLICY_CREATED ||
                                                  type == NotificationType.POLICY_UPDATED ||
                                                  type == NotificationType.POLICY_DELETED)) {
                        continue;
                    }
                    
                    Notification notification = new Notification(userId, type, title, message);
                    notification = notificationRepository.save(notification);
                    
                    // Send email asynchronously (don't wait)
                    try {
                        emailService.sendNotificationEmail(userId, title, message);
                        notification.setEmailSent(true);
                        notificationRepository.save(notification);
                    } catch (Exception e) {
                        logger.error("Failed to send email notification to user {}: {}", userId, e.getMessage());
                    }
                    
                    successCount++;
                } catch (Exception e) {
                    logger.error("Failed to create notification for user {}: {}", user.get("id"), e.getMessage());
                    failureCount++;
                }
            }
            
            logger.info("Created notifications for {} users ({} success, {} failures)", 
                       users.size(), successCount, failureCount);
        } catch (Exception e) {
            logger.error("Failed to fetch users or create notifications: {}", e.getMessage());
        }
    }
}





