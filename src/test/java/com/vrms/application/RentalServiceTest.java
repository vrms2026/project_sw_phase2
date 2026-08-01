package com.vrms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vrms.domain.Rental;
import com.vrms.domain.RentalCostStrategy;
import com.vrms.domain.RentalStatus;
import com.vrms.domain.StandardStrategy;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.FileRentalRepository;
import com.vrms.persistence.FileVehicleRepository;

public class RentalServiceTest {

    @TempDir
    Path tempDir;

    private FileVehicleRepository vehicleRepository;
    private FileRentalRepository rentalRepository;
    private DateProvider dateProvider;
    private RentalService rentalService;

    @BeforeEach
    public void setUp() {
        vehicleRepository =
                new FileVehicleRepository(
                        tempDir.resolve("vehicles.txt")
                );

        rentalRepository =
                new FileRentalRepository(
                        tempDir.resolve("rentals.txt"),
                        vehicleRepository
                );

        dateProvider = mock(DateProvider.class);

        when(dateProvider.getCurrentDate())
                .thenReturn(LocalDate.of(2026, 7, 15));

        rentalService = new RentalService(
                vehicleRepository,
                rentalRepository,
                dateProvider
        );
    }

    @Test
    public void rentVehicle_whenVehicleIsAvailable_shouldCreateRentalAndChangeStatus() {
        Rental rental = rentalService.rentVehicle(
                "R1",
                "V1",
                "Ahmad",
                "ahmad@example.com",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15)
        );

        assertEquals("R1", rental.getRentalId());
        assertEquals("Ahmad", rental.getCustomerName());
        assertEquals(
                "ahmad@example.com",
                rental.getCustomerEmail()
        );
        assertEquals(
                RentalStatus.ACTIVE,
                rental.getStatus()
        );
        assertEquals(
                VehicleStatus.RENTED,
                rental.getVehicle().getStatus()
        );
        assertEquals(
                1,
                rentalRepository.findAll().size()
        );

        Vehicle savedVehicle =
                vehicleRepository.findById("V1");

        assertEquals(
                VehicleStatus.RENTED,
                savedVehicle.getStatus()
        );
    }

    @Test
    public void rentVehicle_whenVehicleStatusIsRented_shouldThrowException() {
        Vehicle vehicle =
                vehicleRepository.findById("V1");

        vehicle.setStatus(VehicleStatus.RENTED);
        vehicleRepository.save(vehicle);

        assertThrows(
                IllegalStateException.class,
                () -> rentalService.rentVehicle(
                        "R2",
                        "V1",
                        "Sara",
                        "sara@example.com",
                        LocalDate.of(2026, 7, 10),
                        LocalDate.of(2026, 7, 15)
                )
        );
    }

    @Test
    public void rentVehicle_whenVehicleDoesNotExist_shouldThrowException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> rentalService.rentVehicle(
                        "R3",
                        "V99",
                        "Omar",
                        "omar@example.com",
                        LocalDate.of(2026, 7, 10),
                        LocalDate.of(2026, 7, 15)
                )
        );
    }

    @Test
    public void rentVehicle_whenStartDateIsNull_shouldThrowException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> rentalService.rentVehicle(
                        "R4",
                        "V1",
                        "Lina",
                        "lina@example.com",
                        null,
                        LocalDate.of(2026, 7, 15)
                )
        );
    }

    @Test
    public void rentVehicle_whenEndDateIsNull_shouldThrowException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> rentalService.rentVehicle(
                        "R5",
                        "V1",
                        "Mona",
                        "mona@example.com",
                        LocalDate.of(2026, 7, 10),
                        null
                )
        );
    }

    @Test
    public void rentVehicle_whenEndDateIsBeforeStartDate_shouldThrowException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> rentalService.rentVehicle(
                        "R6",
                        "V1",
                        "Khaled",
                        "khaled@example.com",
                        LocalDate.of(2026, 7, 15),
                        LocalDate.of(2026, 7, 10)
                )
        );
    }

    @Test
    public void rentVehicle_whenEndDateEqualsStartDate_shouldThrowException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> rentalService.rentVehicle(
                        "R7",
                        "V1",
                        "Noor",
                        "noor@example.com",
                        LocalDate.of(2026, 7, 10),
                        LocalDate.of(2026, 7, 10)
                )
        );
    }

    @Test
    public void rentVehicle_whenRentalPeriodExceedsThirtyDays_shouldThrowException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> rentalService.rentVehicle(
                        "R8",
                        "V1",
                        "Huda",
                        "huda@example.com",
                        LocalDate.of(2026, 7, 1),
                        LocalDate.of(2026, 8, 15)
                )
        );
    }

    @Test
    public void rentVehicle_whenRentalPeriodIsExactlyThirtyDays_shouldCreateRental() {
        Rental rental = rentalService.rentVehicle(
                "R9",
                "V1",
                "Rana",
                "rana@example.com",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 31)
        );

        assertEquals(
                RentalStatus.ACTIVE,
                rental.getStatus()
        );
        assertEquals(
                VehicleStatus.RENTED,
                rental.getVehicle().getStatus()
        );
        assertEquals(
                1,
                rentalRepository.findAll().size()
        );

        Vehicle savedVehicle =
                vehicleRepository.findById("V1");

        assertEquals(
                VehicleStatus.RENTED,
                savedVehicle.getStatus()
        );
    }

    @Test
    public void rentVehicle_whenVehicleHasActiveRental_shouldThrowException() {
        Vehicle vehicle =
                vehicleRepository.findById("V1");

        Rental existingRental = new Rental(
                "R10",
                vehicle,
                "Rawan",
                "rawan@example.com",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                RentalStatus.ACTIVE
        );

        rentalRepository.save(existingRental);

        assertThrows(
                IllegalStateException.class,
                () -> rentalService.rentVehicle(
                        "R11",
                        "V1",
                        "Dana",
                        "dana@example.com",
                        LocalDate.of(2026, 7, 6),
                        LocalDate.of(2026, 7, 10)
                )
        );
    }

    @Test
    public void rentVehicle_whenPreviousRentalIsClosed_shouldCreateNewRental() {
        Vehicle vehicle =
                vehicleRepository.findById("V1");

        Rental closedRental = new Rental(
                "R12",
                vehicle,
                "Lama",
                "lama@example.com",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 5),
                RentalStatus.CLOSED
        );

        rentalRepository.save(closedRental);

        vehicle.setStatus(
                VehicleStatus.AVAILABLE
        );
        vehicleRepository.save(vehicle);

        Rental newRental = rentalService.rentVehicle(
                "R13",
                "V1",
                "Aya",
                "aya@example.com",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15)
        );

        assertEquals(
                RentalStatus.ACTIVE,
                newRental.getStatus()
        );
        assertEquals(
                2,
                rentalRepository.findAll().size()
        );

        Vehicle savedVehicle =
                vehicleRepository.findById("V1");

        assertEquals(
                VehicleStatus.RENTED,
                savedVehicle.getStatus()
        );
    }

    @Test
    public void setRentalStrategy_shouldStoreRentalStrategy() {
        RentalCostStrategy strategy =
                (rental, returnDate) -> 100.0;

        rentalService.setRentalStrategy(strategy);

        assertSame(
                strategy,
                rentalService.getRentalStrategy()
        );
    }

    @Test
    public void returnVehicle_whenStrategyIsSet_shouldCloseRentalAndCalculateCost() {
        Rental rental = rentalService.rentVehicle(
                "R14",
                "V1",
                "Ahmad",
                "ahmad@example.com",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15)
        );

        rentalService.setRentalStrategy(
                (currentRental, returnDate) -> 250.0
        );

        Rental returnedRental =
                rentalService.returnVehicle(
                        "V1",
                        LocalDate.of(2026, 7, 15)
                );

        assertEquals(
                rental.getRentalId(),
                returnedRental.getRentalId()
        );

        assertEquals(
                250.0,
                returnedRental.getTotalCost(),
                0.001
        );

        assertEquals(
                RentalStatus.CLOSED,
                returnedRental.getStatus()
        );

        assertEquals(
                VehicleStatus.AVAILABLE,
                returnedRental.getVehicle().getStatus()
        );

        Vehicle savedVehicle =
                vehicleRepository.findById("V1");

        assertEquals(
                VehicleStatus.AVAILABLE,
                savedVehicle.getStatus()
        );

        Rental savedRental =
                rentalRepository.findById("R14");

        assertEquals(
                RentalStatus.CLOSED,
                savedRental.getStatus()
        );

        assertEquals(
                250.0,
                savedRental.getTotalCost(),
                0.001
        );
    }

    @Test
    public void returnVehicle_whenStrategyIsNotSet_shouldThrowException() {
        Rental rental = rentalService.rentVehicle(
                "R15",
                "V1",
                "Sara",
                "sara@example.com",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15)
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rentalService
                                .returnVehicle("V1")
                );

        assertEquals(
                "No rental cost strategy is set.",
                exception.getMessage()
        );

        assertEquals(
                RentalStatus.ACTIVE,
                rental.getStatus()
        );
        assertEquals(
                VehicleStatus.RENTED,
                rental.getVehicle().getStatus()
        );
        assertEquals(
                0.0,
                rental.getTotalCost(),
                0.001
        );
    }

    @Test
    public void returnVehicle_whenRentalDoesNotExist_shouldThrowException() {
        rentalService.setRentalStrategy(
                (rental, returnDate) -> 100.0
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rentalService
                                .returnVehicle("V1")
                );

        assertEquals(
                "Rental for vehicle not found.",
                exception.getMessage()
        );
    }

    @Test
    public void returnVehicle_shouldPassRentalToSelectedStrategy() {
        Rental rental = rentalService.rentVehicle(
                "R16",
                "V1",
                "Omar",
                "omar@example.com",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15)
        );

        Rental[] receivedRental =
                new Rental[1];

        rentalService.setRentalStrategy(
                (currentRental, returnDate) -> {
                    receivedRental[0] =
                            currentRental;

                    return 180.0;
                }
        );

        rentalService.returnVehicle("V1");

        assertEquals(
                rental.getRentalId(),
                receivedRental[0].getRentalId()
        );
    }

    @Test
    public void returnVehicle_whenReturnedLate_shouldUseProvidedReturnDate() {
        rentalService.rentVehicle(
                "R17",
                "V1",
                "Lina",
                "lina@example.com",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5)
        );

        rentalService.setRentalStrategy(
                new StandardStrategy()
        );

        Rental returnedRental =
                rentalService.returnVehicle(
                        "V1",
                        LocalDate.of(2026, 7, 7)
                );

        assertEquals(
                200.0,
                returnedRental.getTotalCost(),
                0.001
        );

        Rental savedRental =
                rentalRepository.findById("R17");

        assertEquals(
                200.0,
                savedRental.getTotalCost(),
                0.001
        );

        assertEquals(
                RentalStatus.CLOSED,
                savedRental.getStatus()
        );
    }

    @Test
    public void rentVehicle_whenRentalIdAlreadyExists_shouldThrowException() {
        rentalService.rentVehicle(
                "R18",
                "V1",
                "Ahmad",
                "ahmad@example.com",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5)
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rentalService.rentVehicle(
                                "R18",
                                "V3",
                                "Sara",
                                "sara@example.com",
                                LocalDate.of(2026, 7, 10),
                                LocalDate.of(2026, 7, 15)
                        )
                );

        assertEquals(
                "Rental ID already exists.",
                exception.getMessage()
        );
    }

    @Test
    public void returnVehicle_whenReturnDateIsBeforeStartDate_shouldThrowException() {
        Rental rental = rentalService.rentVehicle(
                "R19",
                "V1",
                "Lina",
                "lina@example.com",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15)
        );

        rentalService.setRentalStrategy(
                new StandardStrategy()
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rentalService.returnVehicle(
                                "V1",
                                LocalDate.of(2026, 7, 9)
                        )
                );

        assertEquals(
                "Return date cannot be before the rental start date.",
                exception.getMessage()
        );

        assertEquals(
                RentalStatus.ACTIVE,
                rental.getStatus()
        );

        assertEquals(
                VehicleStatus.RENTED,
                rental.getVehicle().getStatus()
        );
    }

    @Test
    public void returnVehicle_withoutProvidedDate_shouldUseDateProvider() {
        rentalService.rentVehicle(
                "R20",
                "V1",
                "Ahmad",
                "ahmad@example.com",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5)
        );

        rentalService.setRentalStrategy(
                new StandardStrategy()
        );

        when(dateProvider.getCurrentDate())
                .thenReturn(
                        LocalDate.of(2026, 7, 7)
                );

        Rental returnedRental =
                rentalService.returnVehicle("V1");

        assertEquals(
                200.0,
                returnedRental.getTotalCost(),
                0.001
        );

        assertEquals(
                RentalStatus.CLOSED,
                returnedRental.getStatus()
        );

        assertEquals(
                VehicleStatus.AVAILABLE,
                returnedRental.getVehicle().getStatus()
        );

        verify(dateProvider).getCurrentDate();
    }
}