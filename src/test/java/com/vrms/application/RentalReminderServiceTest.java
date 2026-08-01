package com.vrms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vrms.domain.Rental;
import com.vrms.domain.RentalStatus;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.FileRentalRepository;
import com.vrms.persistence.FileVehicleRepository;

public class RentalReminderServiceTest {

    @TempDir
    Path tempDir;

    private NotificationService notificationService;
    private RentalReminderService reminderService;
    private FileVehicleRepository vehicleRepository;
    private FileRentalRepository rentalRepository;

    @BeforeEach
    public void setUp() {
        vehicleRepository = new FileVehicleRepository(
                tempDir.resolve("vehicles.txt")
        );

        rentalRepository = new FileRentalRepository(
                tempDir.resolve("rentals.txt"),
                vehicleRepository
        );

        notificationService = mock(
                NotificationService.class
        );

        reminderService = new RentalReminderService(
                notificationService,
                rentalRepository
        );
    }

    @Test
    public void checkReminder_expiresInTwoDays_sendsNotification() {
        Rental rental = createRental(
                "R1",
                "V1",
                "haneen@example.com",
                LocalDate.of(2026, 7, 16),
                RentalStatus.ACTIVE
        );

        boolean reminderGenerated =
                reminderService.checkAndSendReminder(
                        rental,
                        LocalDate.of(2026, 7, 14)
                );

        assertTrue(reminderGenerated);

        verify(notificationService).sendNotification(
                eq("haneen@example.com"),
                eq("Rental Expiry Reminder"),
                contains(
                        "will expire in two days on 2026-07-16"
                )
        );
    }

    @Test
    public void checkReminder_expiresToday_sendsNotification() {
        Rental rental = createRental(
                "R2",
                "V2",
                "sara@example.com",
                LocalDate.of(2026, 7, 14),
                RentalStatus.ACTIVE
        );

        boolean reminderGenerated =
                reminderService.checkAndSendReminder(
                        rental,
                        LocalDate.of(2026, 7, 14)
                );

        assertTrue(reminderGenerated);

        verify(notificationService).sendNotification(
                eq("sara@example.com"),
                eq("Rental Expired"),
                contains("expires today")
        );
    }

    @Test
    public void checkReminder_expiresTomorrow_doesNotSendNotification() {
        Rental rental = createRental(
                "R3",
                "V3",
                "omar@example.com",
                LocalDate.of(2026, 7, 15),
                RentalStatus.ACTIVE
        );

        boolean reminderGenerated =
                reminderService.checkAndSendReminder(
                        rental,
                        LocalDate.of(2026, 7, 14)
                );

        assertFalse(reminderGenerated);
        verifyNoInteractions(notificationService);
    }

    @Test
    public void checkReminder_expiryIsFar_doesNotSendNotification() {
        Rental rental = createRental(
                "R4",
                "V4",
                "lina@example.com",
                LocalDate.of(2026, 7, 20),
                RentalStatus.ACTIVE
        );

        boolean reminderGenerated =
                reminderService.checkAndSendReminder(
                        rental,
                        LocalDate.of(2026, 7, 14)
                );

        assertFalse(reminderGenerated);
        verifyNoInteractions(notificationService);
    }

    @Test
    public void checkReminder_alreadyExpired_doesNotSendNotification() {
        Rental rental = createRental(
                "R5",
                "V1",
                "mona@example.com",
                LocalDate.of(2026, 7, 10),
                RentalStatus.ACTIVE
        );

        boolean reminderGenerated =
                reminderService.checkAndSendReminder(
                        rental,
                        LocalDate.of(2026, 7, 14)
                );

        assertFalse(reminderGenerated);
        verifyNoInteractions(notificationService);
    }

    @Test
    public void checkReminder_closedRental_doesNotSendNotification() {
        Rental rental = createRental(
                "R6",
                "V1",
                "aya@example.com",
                LocalDate.of(2026, 7, 16),
                RentalStatus.CLOSED
        );

        boolean reminderGenerated =
                reminderService.checkAndSendReminder(
                        rental,
                        LocalDate.of(2026, 7, 14)
                );

        assertFalse(reminderGenerated);
        verifyNoInteractions(notificationService);
    }

    @Test
    public void checkAllRentals_eligibleRentals_returnsReminderCount() {
        saveRental(
                createRental(
                        "R7",
                        "V1",
                        "customer1@example.com",
                        LocalDate.of(2026, 7, 16),
                        RentalStatus.ACTIVE
                )
        );

        saveRental(
                createRental(
                        "R8",
                        "V2",
                        "customer2@example.com",
                        LocalDate.of(2026, 7, 14),
                        RentalStatus.ACTIVE
                )
        );

        saveRental(
                createRental(
                        "R9",
                        "V3",
                        "customer3@example.com",
                        LocalDate.of(2026, 7, 20),
                        RentalStatus.ACTIVE
                )
        );

        saveRental(
                createRental(
                        "R10",
                        "V4",
                        "customer4@example.com",
                        LocalDate.of(2026, 7, 16),
                        RentalStatus.CLOSED
                )
        );

        int remindersGenerated =
                reminderService.checkAllRentalsAndSendReminders(
                        LocalDate.of(2026, 7, 14)
                );

        assertEquals(2, remindersGenerated);

        verify(notificationService).sendNotification(
                anyString(),
                eq("Rental Expiry Reminder"),
                contains("will expire in two days")
        );

        verify(notificationService).sendNotification(
                anyString(),
                eq("Rental Expired"),
                contains("expires today")
        );

        verify(
                notificationService,
                times(2)
        ).sendNotification(
                anyString(),
                anyString(),
                anyString()
        );
    }

    @Test
    public void addObserver_secondObserver_notifiesBothObservers() {
        NotificationService secondObserver = mock(
                NotificationService.class
        );

        reminderService.addObserver(secondObserver);

        Rental rental = createRental(
                "R11",
                "V1",
                "rawan@example.com",
                LocalDate.of(2026, 7, 16),
                RentalStatus.ACTIVE
        );

        boolean reminderGenerated =
                reminderService.checkAndSendReminder(
                        rental,
                        LocalDate.of(2026, 7, 14)
                );

        assertTrue(reminderGenerated);

        verify(notificationService).sendNotification(
                eq("rawan@example.com"),
                eq("Rental Expiry Reminder"),
                contains("will expire in two days")
        );

        verify(secondObserver).sendNotification(
                eq("rawan@example.com"),
                eq("Rental Expiry Reminder"),
                contains("will expire in two days")
        );
    }

    @Test
    public void removeObserver_removedObserver_isNotNotified() {
        NotificationService secondObserver = mock(
                NotificationService.class
        );

        reminderService.addObserver(secondObserver);
        reminderService.removeObserver(secondObserver);

        Rental rental = createRental(
                "R12",
                "V1",
                "dana@example.com",
                LocalDate.of(2026, 7, 16),
                RentalStatus.ACTIVE
        );

        boolean reminderGenerated =
                reminderService.checkAndSendReminder(
                        rental,
                        LocalDate.of(2026, 7, 14)
                );

        assertTrue(reminderGenerated);

        verify(notificationService).sendNotification(
                eq("dana@example.com"),
                eq("Rental Expiry Reminder"),
                contains("will expire in two days")
        );

        verifyNoInteractions(secondObserver);
    }

    @Test
    public void addObserver_nullObserver_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> reminderService.addObserver(null)
        );
    }

    @Test
    public void checkReminder_nullRental_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> reminderService.checkAndSendReminder(
                        null,
                        LocalDate.of(2026, 7, 14)
                )
        );
    }

    @Test
    public void checkReminder_nullCurrentDate_throwsException() {
        Rental rental = createRental(
                "R13",
                "V1",
                "aya@example.com",
                LocalDate.of(2026, 7, 16),
                RentalStatus.ACTIVE
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> reminderService.checkAndSendReminder(
                        rental,
                        null
                )
        );
    }

    @Test
    public void checkAllRentals_nullCurrentDate_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> reminderService
                        .checkAllRentalsAndSendReminders(null)
        );
    }

    @Test
    public void checkAllRentals_repositoryNotConfigured_throwsException() {
        RentalReminderService serviceWithoutRepository =
                new RentalReminderService(
                        notificationService
                );

        assertThrows(
                IllegalStateException.class,
                () -> serviceWithoutRepository
                        .checkAllRentalsAndSendReminders(
                                LocalDate.of(2026, 7, 14)
                        )
        );
    }

    private Rental createRental(
            String rentalId,
            String vehicleId,
            String customerEmail,
            LocalDate endDate,
            RentalStatus status) {

        VehicleStatus vehicleStatus =
                status == RentalStatus.ACTIVE
                        ? VehicleStatus.RENTED
                        : VehicleStatus.AVAILABLE;

        Vehicle vehicle = new Vehicle(
                vehicleId,
                "Toyota",
                "Corolla",
                40.0,
                vehicleStatus
        );

        return new Rental(
                rentalId,
                vehicle,
                "Customer",
                customerEmail,
                LocalDate.of(2026, 7, 1),
                endDate,
                status
        );
    }

    private void saveRental(Rental rental) {
        vehicleRepository.save(
                rental.getVehicle()
        );

        rentalRepository.save(rental);
    }
}