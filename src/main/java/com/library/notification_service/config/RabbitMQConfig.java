package com.library.notification_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for listening to booking events
 */
@Configuration
public class RabbitMQConfig {
    
    // Exchange names
    public static final String BOOKING_EXCHANGE = "booking.events";
    public static final String RESOURCE_EXCHANGE = "resource.events";
    public static final String POLICY_EXCHANGE = "policy.events";
    
    // Queue names
    public static final String BOOKING_CREATED_QUEUE = "booking.created";
    public static final String BOOKING_CANCELED_QUEUE = "booking.canceled";
    public static final String BOOKING_CHECKED_IN_QUEUE = "booking.checked_in";
    public static final String BOOKING_NO_SHOW_QUEUE = "booking.no_show";
    public static final String RESOURCE_CREATED_QUEUE = "notification.resource.created";
    public static final String RESOURCE_DELETED_QUEUE = "notification.resource.deleted";
    public static final String POLICY_CREATED_QUEUE = "notification.policy.created";
    public static final String POLICY_UPDATED_QUEUE = "notification.policy.updated";
    public static final String POLICY_DELETED_QUEUE = "notification.policy.deleted";
    
    // Routing keys
    public static final String BOOKING_CREATED_ROUTING_KEY = "booking.created";
    public static final String BOOKING_CANCELED_ROUTING_KEY = "booking.canceled";
    public static final String BOOKING_CHECKED_IN_ROUTING_KEY = "booking.checked_in";
    public static final String BOOKING_NO_SHOW_ROUTING_KEY = "booking.no_show";
    public static final String RESOURCE_CREATED_ROUTING_KEY = "resource.created";
    public static final String RESOURCE_DELETED_ROUTING_KEY = "resource.deleted";
    public static final String POLICY_CREATED_ROUTING_KEY = "policy.created";
    public static final String POLICY_UPDATED_ROUTING_KEY = "policy.updated";
    public static final String POLICY_DELETED_ROUTING_KEY = "policy.deleted";
    
    /**
     * Declare topic exchanges (if not exists)
     */
    @Bean
    public TopicExchange bookingExchange() {
        return new TopicExchange(BOOKING_EXCHANGE, true, false);
    }
    
    @Bean
    public TopicExchange resourceExchange() {
        return new TopicExchange(RESOURCE_EXCHANGE, true, false);
    }
    
    @Bean
    public TopicExchange policyExchange() {
        return new TopicExchange(POLICY_EXCHANGE, true, false);
    }
    
    /**
     * Create queues for booking events
     */
    @Bean
    public Queue bookingCreatedQueue() {
        return new Queue(BOOKING_CREATED_QUEUE, true);
    }
    
    @Bean
    public Queue bookingCanceledQueue() {
        return new Queue(BOOKING_CANCELED_QUEUE, true);
    }
    
    @Bean
    public Queue bookingCheckedInQueue() {
        return new Queue(BOOKING_CHECKED_IN_QUEUE, true);
    }
    
    @Bean
    public Queue bookingNoShowQueue() {
        return new Queue(BOOKING_NO_SHOW_QUEUE, true);
    }
    
    @Bean
    public Queue resourceCreatedQueue() {
        return new Queue(RESOURCE_CREATED_QUEUE, true);
    }
    
    @Bean
    public Queue resourceDeletedQueue() {
        return new Queue(RESOURCE_DELETED_QUEUE, true);
    }
    
    @Bean
    public Queue policyCreatedQueue() {
        return new Queue(POLICY_CREATED_QUEUE, true);
    }
    
    @Bean
    public Queue policyUpdatedQueue() {
        return new Queue(POLICY_UPDATED_QUEUE, true);
    }
    
    @Bean
    public Queue policyDeletedQueue() {
        return new Queue(POLICY_DELETED_QUEUE, true);
    }
    
    /**
     * Bind queues to exchanges
     */
    @Bean
    public Binding bookingCreatedBinding() {
        return BindingBuilder
            .bind(bookingCreatedQueue())
            .to(bookingExchange())
            .with(BOOKING_CREATED_ROUTING_KEY);
    }
    
    @Bean
    public Binding bookingCanceledBinding() {
        return BindingBuilder
            .bind(bookingCanceledQueue())
            .to(bookingExchange())
            .with(BOOKING_CANCELED_ROUTING_KEY);
    }
    
    @Bean
    public Binding bookingCheckedInBinding() {
        return BindingBuilder
            .bind(bookingCheckedInQueue())
            .to(bookingExchange())
            .with(BOOKING_CHECKED_IN_ROUTING_KEY);
    }
    
    @Bean
    public Binding bookingNoShowBinding() {
        return BindingBuilder
            .bind(bookingNoShowQueue())
            .to(bookingExchange())
            .with(BOOKING_NO_SHOW_ROUTING_KEY);
    }
    
    @Bean
    public Binding resourceCreatedBinding() {
        return BindingBuilder
            .bind(resourceCreatedQueue())
            .to(resourceExchange())
            .with(RESOURCE_CREATED_ROUTING_KEY);
    }
    
    @Bean
    public Binding resourceDeletedBinding() {
        return BindingBuilder
            .bind(resourceDeletedQueue())
            .to(resourceExchange())
            .with(RESOURCE_DELETED_ROUTING_KEY);
    }
    
    @Bean
    public Binding policyCreatedBinding() {
        return BindingBuilder
            .bind(policyCreatedQueue())
            .to(policyExchange())
            .with(POLICY_CREATED_ROUTING_KEY);
    }
    
    @Bean
    public Binding policyUpdatedBinding() {
        return BindingBuilder
            .bind(policyUpdatedQueue())
            .to(policyExchange())
            .with(POLICY_UPDATED_ROUTING_KEY);
    }
    
    @Bean
    public Binding policyDeletedBinding() {
        return BindingBuilder
            .bind(policyDeletedQueue())
            .to(policyExchange())
            .with(POLICY_DELETED_ROUTING_KEY);
    }
    
    /**
     * JSON message converter
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    /**
     * RabbitTemplate with JSON converter
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}





