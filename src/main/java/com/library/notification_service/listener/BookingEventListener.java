package com.library.notification_service.listener;

import com.library.notification_service.config.RabbitMQConfig;
import com.library.notification_service.dto.BookingEvent;
import com.library.notification_service.entity.NotificationType;
import com.library.notification_service.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ listener for booking events
 */
@Component
public class BookingEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(BookingEventListener.class);
    
    private final NotificationService notificationService;
    
    public BookingEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    /**
     * Listen to booking.created events
     */
    @RabbitListener(queues = RabbitMQConfig.BOOKING_CREATED_QUEUE)
    public void handleBookingCreated(BookingEvent event) {
        logger.info("Received booking.created event for booking: {}", event.getId());
        
        String title = "Booking Confirmed";
        String message = String.format(
            "Your booking has been confirmed!\n\n" +
            "Booking ID: %d\n" +
            "Resource ID: %d\n" +
            "Start Time: %s\n" +
            "End Time: %s\n" +
            "QR Code: %s\n\n" +
            "Please arrive on time and use your QR code for check-in.",
            event.getId(),
            event.getResourceId(),
            event.getStartTime(),
            event.getEndTime(),
            event.getQrCode()
        );
        
        notificationService.createNotification(
            event.getUserId(),
            NotificationType.BOOKING_CONFIRMED,
            title,
            message
        );
    }
    
    /**
     * Listen to booking.canceled events
     */
    @RabbitListener(queues = RabbitMQConfig.BOOKING_CANCELED_QUEUE)
    public void handleBookingCanceled(BookingEvent event) {
        logger.info("Received booking.canceled event for booking: {}", event.getId());
        
        String title = "Booking Canceled";
        String message = String.format(
            "Your booking has been canceled.\n\n" +
            "Booking ID: %d\n" +
            "Resource ID: %d\n" +
            "Original Time: %s to %s",
            event.getId(),
            event.getResourceId(),
            event.getStartTime(),
            event.getEndTime()
        );
        
        notificationService.createNotification(
            event.getUserId(),
            NotificationType.BOOKING_CANCELED,
            title,
            message
        );
    }
    
    /**
     * Listen to booking.checked_in events
     */
    @RabbitListener(queues = RabbitMQConfig.BOOKING_CHECKED_IN_QUEUE)
    public void handleBookingCheckedIn(BookingEvent event) {
        logger.info("Received booking.checked_in event for booking: {}", event.getId());
        
        String title = "Check-In Successful";
        String message = String.format(
            "You have successfully checked in!\n\n" +
            "Booking ID: %d\n" +
            "Resource ID: %d\n" +
            "Check-in Time: %s\n\n" +
            "Enjoy your study session!",
            event.getId(),
            event.getResourceId(),
            event.getCheckedInAt()
        );
        
        notificationService.createNotification(
            event.getUserId(),
            NotificationType.CHECK_IN_REMINDER,
            title,
            message
        );
    }
    
    /**
     * Listen to booking.no_show events
     */
    @RabbitListener(queues = RabbitMQConfig.BOOKING_NO_SHOW_QUEUE)
    public void handleBookingNoShow(BookingEvent event) {
        logger.info("Received booking.no_show event for booking: {}", event.getId());
        
        String title = "No-Show Alert";
        String message = String.format(
            "You did not check in for your booking.\n\n" +
            "Booking ID: %d\n" +
            "Resource ID: %d\n" +
            "Scheduled Time: %s to %s\n\n" +
            "The booking has been released. Please book again if you need the resource.",
            event.getId(),
            event.getResourceId(),
            event.getStartTime(),
            event.getEndTime()
        );
        
        notificationService.createNotification(
            event.getUserId(),
            NotificationType.NO_SHOW_ALERT,
            title,
            message
        );
    }
}




