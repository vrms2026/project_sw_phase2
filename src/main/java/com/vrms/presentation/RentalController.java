package com.vrms.presentation;

import java.time.LocalDate;
import java.util.Objects;

import com.vrms.application.RentalService;
import com.vrms.domain.Rental;
import com.vrms.domain.RentalValidationData;

/**
 * Handles vehicle rental and return requests from the
 * presentation layer.
 *
 * <p>The controller delegates the application logic to
 * {@link RentalService}.</p>
 */
public class RentalController {

    /**
     * Service used to perform rental and return operations.
     */
    private final RentalService rentalService;

    /**
     * Creates a rental controller.
     *
     * @param rentalService service used for rental operations
     * @throws NullPointerException if the rental service is null
     */
    public RentalController(
            RentalService rentalService) {

        this.rentalService = Objects.requireNonNull(
                rentalService,
                "Rental service cannot be null."
        );
    }

    /**
     * Rents a vehicle using default validation information.
     *
     * <p>This method is suitable for vehicle types that do not
     * require additional validation information.</p>
     *
     * @param rentalId unique rental identifier
     * @param vehicleId selected vehicle identifier
     * @param customerName customer name
     * @param customerEmail customer email
     * @param startDate rental start date
     * @param endDate rental end date
     * @return the newly created rental
     */
    public Rental rentVehicle(
            String rentalId,
            String vehicleId,
            String customerName,
            String customerEmail,
            LocalDate startDate,
            LocalDate endDate) {

        return rentalService.rentVehicle(
                rentalId,
                vehicleId,
                customerName,
                customerEmail,
                startDate,
                endDate
        );
    }

    /**
     * Rents a vehicle using type-specific validation information.
     *
     * @param rentalId unique rental identifier
     * @param vehicleId selected vehicle identifier
     * @param customerName customer name
     * @param customerEmail customer email
     * @param startDate rental start date
     * @param endDate rental end date
     * @param validationData vehicle-specific validation information
     * @return the newly created rental
     */
    public Rental rentVehicle(
            String rentalId,
            String vehicleId,
            String customerName,
            String customerEmail,
            LocalDate startDate,
            LocalDate endDate,
            RentalValidationData validationData) {

        return rentalService.rentVehicle(
                rentalId,
                vehicleId,
                customerName,
                customerEmail,
                startDate,
                endDate,
                validationData
        );
    }

    /**
     * Returns a rented vehicle using the current date.
     *
     * @param vehicleId identifier of the returned vehicle
     * @return the closed rental record
     */
    public Rental returnVehicle(String vehicleId) {
        return rentalService.returnVehicle(vehicleId);
    }
}