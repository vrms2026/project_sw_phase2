package com.vrms.application;

/**
 * Defines a service for sending notifications to customers.
 *
 * <p>Different implementations may send notifications using
 * email or another notification method.</p>
 */
public interface NotificationService {

    /**
     * Sends a notification to the specified recipient.
     *
     * @param recipientEmail recipient email address
     * @param subject notification subject
     * @param message notification message
     */
    void sendNotification(
            String recipientEmail,
            String subject,
            String message
    );
}