package com.library.notification_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending email notifications
 */
@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.from:noreply@library-system.edu}")
    private String fromEmail;
    
    @Value("${spring.mail.enabled:false}")
    private boolean emailEnabled;
    
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    /**
     * Send notification email
     * In development, this will just log. Configure SMTP in production.
     */
    public void sendNotificationEmail(Long userId, String subject, String message) {
        if (!emailEnabled) {
            logger.info("Email sending is disabled. Would send email to user {}: Subject: {}, Message: {}", 
                       userId, subject, message);
            return;
        }
        
        try {
            // In a real system, you would fetch user email from User Service
            // For now, we'll use a placeholder
            String toEmail = "user" + userId + "@university.edu";
            
            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom(fromEmail);
            email.setTo(toEmail);
            email.setSubject(subject);
            email.setText(message);
            
            mailSender.send(email);
            logger.info("Email sent successfully to user: {}", userId);
        } catch (Exception e) {
            logger.error("Failed to send email to user {}: {}", userId, e.getMessage());
            throw e;
        }
    }
}

