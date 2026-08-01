package com.vrms.application;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.vrms.domain.Rental;
import com.vrms.domain.RentalStatus;
import com.vrms.persistence.RentalRepository;

/**
 * Checks active rentals and sends expiry reminders.
 *
 * <p>The service uses the Observer Pattern. Registered notification
 * observers are notified when an active rental is close to its end
 * date or expires on the current date.</p>
 */
public class RentalReminderService {

    /**
     * Number of days before expiry when an advance reminder is sent.
     */
    private static final int REMINDER_DAYS_BEFORE_EXPIRY = 2;

    /**
     * Registered notification observers.
     */
    private final List<NotificationService> observers;

    /**
     * Repository used to retrieve stored rental records.
     */
    private final RentalRepository rentalRepository;

    /**
     * Creates a reminder service for checking individual rentals.
     *
     * <p>This constructor does not configure a rental repository,
     * so {@link #checkAllRentalsAndSendReminders(LocalDate)}
     * cannot be used.</p>
     *
     * @param notificationService initial notification observer
     */
    public RentalReminderService(
            NotificationService notificationService) {

        this(notificationService, null);
    }

    /**
     * Creates a reminder service with an initial observer and
     * a rental repository.
     *
     * @param notificationService initial notification observer
     * @param rentalRepository repository containing stored rentals
     */
    public RentalReminderService(
            NotificationService notificationService,
            RentalRepository rentalRepository) {

        this.observers = new ArrayList<>();
        this.rentalRepository = rentalRepository;

        addObserver(notificationService);
    }

    /**
     * Registers a notification observer.
     *
     * @param observer notification observer to register
     * @throws IllegalArgumentException if the observer is null
     */
    public void addObserver(NotificationService observer) {
        if (observer == null) {
            throw new IllegalArgumentException(
                    "Notification observer cannot be null."
            );
        }

        observers.add(observer);
    }

    /**
     * Removes a registered notification observer.
     *
     * @param observer notification observer to remove
     */
    public void removeObserver(NotificationService observer) {
        observers.remove(observer);
    }

    /**
     * Checks all stored rentals and sends applicable reminders.
     *
     * @param currentDate date used to check rental expiry
     * @return the number of rentals for which reminders were generated
     * @throws IllegalArgumentException if the current date is null
     * @throws IllegalStateException if no rental repository is configured
     */
    public int checkAllRentalsAndSendReminders(
            LocalDate currentDate) {

        if (currentDate == null) {
            throw new IllegalArgumentException(
                    "Current date cannot be null."
            );
        }

        if (rentalRepository == null) {
            throw new IllegalStateException(
                    "Rental repository is not configured."
            );
        }

        int remindersGenerated = 0;

        for (Rental rental : rentalRepository.findAll()) {
            if (checkAndSendReminder(rental, currentDate)) {
                remindersGenerated++;
            }
        }

        return remindersGenerated;
    }

    /**
     * Checks one rental and sends a reminder when applicable.
     *
     * @param rental rental record to check
     * @param currentDate date used to check rental expiry
     * @return true if a reminder was generated, otherwise false
     * @throws IllegalArgumentException if the rental or date is null
     */
    public boolean checkAndSendReminder(
            Rental rental,
            LocalDate currentDate) {

        if (rental == null) {
            throw new IllegalArgumentException(
                    "Rental cannot be null."
            );
        }

        if (currentDate == null) {
            throw new IllegalArgumentException(
                    "Current date cannot be null."
            );
        }

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            return false;
        }

        long daysUntilExpiry = ChronoUnit.DAYS.between(
                currentDate,
                rental.getEndDate()
        );

        String subject;
        String message;

        if (daysUntilExpiry
                == REMINDER_DAYS_BEFORE_EXPIRY) {

            subject = "Rental Expiry Reminder";

            message = "Hello "
                    + rental.getCustomerName()
                    + ", your rental for "
                    + rental.getVehicle().getBrand()
                    + " "
                    + rental.getVehicle().getModel()
                    + " will expire in two days on "
                    + rental.getEndDate()
                    + ".";

        } else if (daysUntilExpiry == 0) {
            subject = "Rental Expired";

            message = "Hello "
                    + rental.getCustomerName()
                    + ", your rental for "
                    + rental.getVehicle().getBrand()
                    + " "
                    + rental.getVehicle().getModel()
                    + " expires today.";

        } else {
            return false;
        }

        notifyObservers(
                rental.getCustomerEmail(),
                subject,
                message
        );

        return true;
    }

    /**
     * Sends a notification through every registered observer.
     *
     * @param recipientEmail recipient email address
     * @param subject notification subject
     * @param message notification message
     */
    private void notifyObservers(
            String recipientEmail,
            String subject,
            String message) {

        for (NotificationService observer : observers) {
            observer.sendNotification(
                    recipientEmail,
                    subject,
                    message
            );
        }
    }
}