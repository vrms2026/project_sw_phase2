package com.vrms.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;

class EmailNotificationServiceTest {

    @Test
    void defaultConstructor_shouldCreateService() {
        assertNotNull(new EmailNotificationService());
    }

    @Test
    void sendNotification_whenUsernameIsNull_shouldThrowException() {
        EmailNotificationService service = new TestEmailNotificationService(null, "password");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.sendNotification("receiver@example.com", "Subject", "Message"));

        assertEquals("EMAIL_USERNAME is missing.", exception.getMessage());
    }

    @Test
    void sendNotification_whenUsernameIsBlank_shouldThrowException() {
        EmailNotificationService service = new TestEmailNotificationService("   ", "password");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.sendNotification("receiver@example.com", "Subject", "Message"));

        assertEquals("EMAIL_USERNAME is missing.", exception.getMessage());
    }

    @Test
    void sendNotification_whenPasswordIsNull_shouldThrowException() {
        EmailNotificationService service = new TestEmailNotificationService("sender@example.com", null);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.sendNotification("receiver@example.com", "Subject", "Message"));

        assertEquals("EMAIL_PASSWORD is missing.", exception.getMessage());
    }

    @Test
    void sendNotification_whenPasswordIsBlank_shouldThrowException() {
        EmailNotificationService service = new TestEmailNotificationService("sender@example.com", "   ");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.sendNotification("receiver@example.com", "Subject", "Message"));

        assertEquals("EMAIL_PASSWORD is missing.", exception.getMessage());
    }

    @Test
    void sendNotification_withValidData_shouldPrepareAndSendEmail() {
        TestEmailNotificationService service = new TestEmailNotificationService("sender@example.com", "app password");

        assertDoesNotThrow(() -> service.sendNotification("receiver@example.com", "Rental reminder", "Your rental ends tomorrow."));

        assertTrue(service.emailSent);
        assertNotNull(service.sentMessage);
    }

    @Test
    void sendNotification_whenSendingFails_shouldThrowIllegalStateException() {
        TestEmailNotificationService service = new TestEmailNotificationService("sender@example.com", "password");
        service.failure = new MessagingException("SMTP failed");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.sendNotification("receiver@example.com", "Subject", "Message"));

        assertTrue(exception.getMessage().contains("SMTP failed"));
        assertSame(service.failure, exception.getCause());
    }

    private static class TestEmailNotificationService extends EmailNotificationService {

        private boolean emailSent;
        private Message sentMessage;
        private MessagingException failure;

        TestEmailNotificationService(String username, String password) {
            super(username, password);
        }

        @Override
        protected void sendEmail(Message email) throws MessagingException {
            if (failure != null) {
                throw failure;
            }

            emailSent = true;
            sentMessage = email;
        }
    }
}