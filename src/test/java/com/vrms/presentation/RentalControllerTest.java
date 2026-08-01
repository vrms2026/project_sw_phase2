package com.vrms.presentation;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vrms.application.RentalService;
import com.vrms.domain.Rental;
import com.vrms.domain.RentalValidationData;

/**
 * Tests rental requests handled by {@link RentalController}.
 */
public class RentalControllerTest {

    /**
     * Mocked rental service used during testing.
     */
    private RentalService rentalService;

    /**
     * Controller under test.
     */
    private RentalController rentalController;

    /**
     * Creates the mock service and controller before each test.
     */
    @BeforeEach
    public void setUp() {
        rentalService = mock(RentalService.class);
        rentalController = new RentalController(rentalService);
    }

    /**
     * Verifies that the controller sends the basic rental data
     * to the service and returns the created rental.
     */
    @Test
    public void rentVehicle_whenRequestIsValid_shouldReturnCreatedRental() {
        LocalDate startDate = LocalDate.of(2026, 7, 10);
        LocalDate endDate = LocalDate.of(2026, 7, 15);

        Rental expectedRental = mock(Rental.class);

        when(rentalService.rentVehicle(
                "R1",
                "V1",
                "Ahmad",
                "ahmad@example.com",
                startDate,
                endDate
        )).thenReturn(expectedRental);

        Rental actualRental = rentalController.rentVehicle(
                "R1",
                "V1",
                "Ahmad",
                "ahmad@example.com",
                startDate,
                endDate
        );

        assertSame(expectedRental, actualRental);

        verify(rentalService).rentVehicle(
                "R1",
                "V1",
                "Ahmad",
                "ahmad@example.com",
                startDate,
                endDate
        );
    }

    /**
     * Verifies that the controller sends vehicle-specific validation
     * information to the rental service.
     */
    @Test
    public void rentVehicle_withValidationData_shouldReturnCreatedRental() {
        LocalDate startDate = LocalDate.of(2026, 7, 10);
        LocalDate endDate = LocalDate.of(2026, 7, 15);

        RentalValidationData validationData =
                new RentalValidationData(
                        21,
                        false,
                        false
                );

        Rental expectedRental = mock(Rental.class);

        when(rentalService.rentVehicle(
                "R2",
                "V2",
                "Sara",
                "sara@example.com",
                startDate,
                endDate,
                validationData
        )).thenReturn(expectedRental);

        Rental actualRental = rentalController.rentVehicle(
                "R2",
                "V2",
                "Sara",
                "sara@example.com",
                startDate,
                endDate,
                validationData
        );

        assertSame(expectedRental, actualRental);

        verify(rentalService).rentVehicle(
                "R2",
                "V2",
                "Sara",
                "sara@example.com",
                startDate,
                endDate,
                validationData
        );
    }

    /**
     * Verifies that the controller returns the result produced by
     * the rental service when a vehicle is returned.
     */
    @Test
    public void returnVehicle_shouldReturnServiceResult() {
        Rental expectedRental = mock(Rental.class);

        when(
                rentalService.returnVehicle("V1")
        ).thenReturn(expectedRental);

        Rental actualRental =
                rentalController.returnVehicle("V1");

        assertSame(expectedRental, actualRental);

        verify(rentalService).returnVehicle("V1");
    }
}