package com.library.notification_service.controller;

import com.library.notification_service.dto.NotificationResponse;
import com.library.common.security.annotation.RequiresOwnership;
import com.library.common.security.annotation.RequiresRole;
import com.library.notification_service.security.annotation.RequiresNotificationOwnership;
import com.library.notification_service.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for notification endpoints
 * Uses AOP annotations for RBAC authorization
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Get notifications by user ID
     * GET /api/notifications/user/{userId}
     * Authorization: AUTHENTICATED
     * Resource Ownership: Users can only view their own notifications, Admins can
     * view any
     */
    @GetMapping("/user/{userId}")
    @RequiresOwnership(resourceIdParam = "userId")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUserId(@PathVariable Long userId) {
        List<NotificationResponse> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get unread notifications by user ID
     * GET /api/notifications/user/{userId}/unread
     * Authorization: AUTHENTICATED
     * Resource Ownership: Users can only view their own notifications, Admins can
     * view any
     */
    @GetMapping("/user/{userId}/unread")
    @RequiresOwnership(resourceIdParam = "userId")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotificationsByUserId(@PathVariable Long userId) {
        List<NotificationResponse> notifications = notificationService.getUnreadNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get unread count for a user
     * GET /api/notifications/user/{userId}/unread/count
     * Authorization: AUTHENTICATED
     * Resource Ownership: Users can only view their own count, Admins can view any
     */
    @GetMapping("/user/{userId}/unread/count")
    @RequiresOwnership(resourceIdParam = "userId")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Mark notification as read
     * PUT /api/notifications/{id}/read
     * Authorization: AUTHENTICATED
     * Resource Ownership: Users can only mark their own notifications as read,
     * Admins can mark any
     */
    @PutMapping("/{id}/read")
    @RequiresRole
    @RequiresNotificationOwnership(notificationIdParam = "id")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id) {
        NotificationResponse response = notificationService.markAsRead(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Mark all notifications as read for a user
     * PUT /api/notifications/user/{userId}/read-all
     * Authorization: AUTHENTICATED
     * Resource Ownership: Users can only mark their own notifications as read,
     * Admins can mark any
     */
    @PutMapping("/user/{userId}/read-all")
    @RequiresRole
    @RequiresOwnership(resourceIdParam = "userId")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Health check endpoint
     * GET /api/notifications/health
     * Authorization: PUBLIC
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification Service is running!");
    }
}
