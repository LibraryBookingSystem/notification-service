package com.library.notification_service.listener;

import com.library.notification_service.config.RabbitMQConfig;
import com.library.notification_service.entity.NotificationType;
import com.library.notification_service.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * RabbitMQ listener for resource events
 */
@Component
public class ResourceEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(ResourceEventListener.class);
    
    private final NotificationService notificationService;
    
    public ResourceEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    /**
     * Listen to resource.created events
     */
    @RabbitListener(queues = RabbitMQConfig.RESOURCE_CREATED_QUEUE)
    public void handleResourceCreated(Map<String, Object> resourceData) {
        logger.info("Received resource.created event for resource: {}", resourceData.get("id"));
        
        String resourceName = resourceData.get("name") != null ? resourceData.get("name").toString() : "Unknown";
        String resourceType = resourceData.get("type") != null ? resourceData.get("type").toString() : "Resource";
        Integer floor = resourceData.get("floor") != null ? Integer.valueOf(resourceData.get("floor").toString()) : null;
        
        String title = "New Resource Available";
        String message = String.format(
            "A new %s has been added to the library!\n\n" +
            "Resource: %s\n" +
            "%s" +
            "\nYou can now book this resource through the floor plan.",
            resourceType,
            resourceName,
            floor != null ? "Floor: " + floor + "\n" : ""
        );
        
        notificationService.createNotificationForAllUsers(
            NotificationType.RESOURCE_CREATED,
            title,
            message
        );
    }
    
    /**
     * Listen to resource.deleted events
     */
    @RabbitListener(queues = RabbitMQConfig.RESOURCE_DELETED_QUEUE)
    public void handleResourceDeleted(Long resourceId) {
        logger.info("Received resource.deleted event for resource: {}", resourceId);
        
        String title = "Resource Removed";
        String message = String.format(
            "A resource has been removed from the library.\n\n" +
            "Resource ID: %d\n\n" +
            "If you had any bookings for this resource, please contact support.",
            resourceId
        );
        
        notificationService.createNotificationForAllUsers(
            NotificationType.RESOURCE_DELETED,
            title,
            message
        );
    }
}

