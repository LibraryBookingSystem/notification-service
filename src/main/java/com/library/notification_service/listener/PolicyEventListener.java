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
 * RabbitMQ listener for policy events
 */
@Component
public class PolicyEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(PolicyEventListener.class);
    
    private final NotificationService notificationService;
    
    public PolicyEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    /**
     * Listen to policy.created events
     */
    @RabbitListener(queues = RabbitMQConfig.POLICY_CREATED_QUEUE)
    public void handlePolicyCreated(Map<String, Object> policyData) {
        logger.info("Received policy.created event for policy: {}", policyData.get("id"));
        
        String policyName = policyData.get("name") != null ? policyData.get("name").toString() : "New Policy";
        
        String title = "New Booking Policy";
        String message = String.format(
            "A new booking policy has been implemented.\n\n" +
            "Policy: %s\n\n" +
            "Please review the updated policies before making your next booking.",
            policyName
        );
        
        notificationService.createNotificationForAllUsers(
            NotificationType.POLICY_CREATED,
            title,
            message
        );
    }
    
    /**
     * Listen to policy.updated events
     */
    @RabbitListener(queues = RabbitMQConfig.POLICY_UPDATED_QUEUE)
    public void handlePolicyUpdated(Map<String, Object> policyData) {
        logger.info("Received policy.updated event for policy: {}", policyData.get("id"));
        
        String policyName = policyData.get("name") != null ? policyData.get("name").toString() : "Policy";
        
        String title = "Booking Policy Updated";
        String message = String.format(
            "A booking policy has been updated.\n\n" +
            "Policy: %s\n\n" +
            "Please review the updated policy details before making your next booking.",
            policyName
        );
        
        notificationService.createNotificationForAllUsers(
            NotificationType.POLICY_UPDATED,
            title,
            message
        );
    }
    
    /**
     * Listen to policy.deleted events
     */
    @RabbitListener(queues = RabbitMQConfig.POLICY_DELETED_QUEUE)
    public void handlePolicyDeleted(Long policyId) {
        logger.info("Received policy.deleted event for policy: {}", policyId);
        
        String title = "Booking Policy Removed";
        String message = String.format(
            "A booking policy has been removed.\n\n" +
            "Policy ID: %d\n\n" +
            "Please check the current policies before making your next booking.",
            policyId
        );
        
        notificationService.createNotificationForAllUsers(
            NotificationType.POLICY_DELETED,
            title,
            message
        );
    }
}

