package com.library.notification_service.repository;

import com.library.notification_service.entity.Notification;
import com.library.notification_service.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Notification entity
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Find notifications by user ID
     */
    List<Notification> findByUserId(Long userId);
    
    /**
     * Find unread notifications by user ID
     */
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
    
    /**
     * Find notifications by type
     */
    List<Notification> findByType(NotificationType type);
    
    /**
     * Count unread notifications for a user
     */
    long countByUserIdAndIsReadFalse(Long userId);
}

