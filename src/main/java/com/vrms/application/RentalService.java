package com.vrms.application;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.vrms.domain.Rental;
import com.vrms.domain.RentalCostStrategy;
import com.vrms.domain.RentalStatus;
import com.vrms.domain.RentalValidationData;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.RentalRepository;
import com.vrms.persistence.VehicleRepository;

/**
 * Provides the application logic required to rent and return vehicles.
 *
 * <p>The service validates rental periods, prevents double booking,
 * applies type-specific vehicle rules, updates vehicle status,
 * saves rental records, closes returned rentals, and calculates
 * rental costs using a selected pricing strategy.</p>
 */
public class RentalService {

    /**
     * Maximum allowed rental duration in days.
     */
    private static final int MAX_RENTAL_DAYS = 30;

    /**
     * Repository used to access and update vehicle records.
     */
    private final VehicleRepository vehicleRepository;

    /**
     * Repository used to store and retrieve rental records.
     */
    private final RentalRepository rentalRepository;

    /**
     * Provider used to obtain the current date.
     */
    private final DateProvider dateProvider;

    /**
     * Strategy used to calculate the rental cost when a vehicle is returned.
     */
    private RentalCostStrategy rentalStrategy;

    /**
     * Creates a rental service using the provided repositories
     * and the real system date.
     *
     * @param vehicleRepository repository containing vehicle records
     * @param rentalRepository repository containing rental records
     * @throws IllegalArgumentException if either repository is null
     */
    public RentalService(
            VehicleRepository vehicleRepository,
            RentalRepository rentalRepository) {

        this(
                vehicleRepository,
                rentalRepository,
                new SystemDateProvider()
        );
    }

    /**
     * Creates a rental service using the provided repositories
     * and date provider.
     *
     * <p>The date provider can be mocked during testing so that
     * date-dependent behavior does not depend on the real system date.</p>
     *
     * @param vehicleRepository repository containing vehicle records
     * @param rentalRepository repository containing rental records
     * @param dateProvider provider used to obtain the current date
     * @throws IllegalArgumentException if any dependency is null
     */
    public RentalService(
            VehicleRepository vehicleRepository,
            RentalRepository rentalRepository,
            DateProvider dateProvider) {

        if (vehicleRepository == null) {
            throw new IllegalArgumentException(
                    "Vehicle repository cannot be null."
            );
        }

        if (rentalRepository == null) {
            throw new IllegalArgumentException(
                    "Rental repository cannot be null."
            );
        }

        if (dateProvider == null) {
            throw new IllegalArgumentException(
                    "Date provider cannot be null."
            );
        }

        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.dateProvider = dateProvider;
        this.rentalStrategy = null;
    }

    /**
     * Sets the strategy used to calculate rental costs.
     *
     * @param rentalStrategy strategy used for rental cost calculation
     */
    public void setRentalStrategy(
            RentalCostStrategy rentalStrategy) {

        this.rentalStrategy = rentalStrategy;
    }

    /**
     * Returns the currently selected rental cost strategy.
     *
     * @return current rental cost strategy
     */
    public RentalCostStrategy getRentalStrategy() {
        return rentalStrategy;
    }

    /**
     * Creates a rental using default validation information.
     *
     * <p>Car and van rentals may use this method because they do not
     * require additional type-specific information.</p>
     *
     * @param rentalId unique rental identifier
     * @param vehicleId identifier of the selected vehicle
     * @param customerName customer name
     * @param customerEmail customer email address
     * @param startDate rental start date
     * @param endDate rental end date
     * @return newly created rental
     */
    public Rental rentVehicle(
            String rentalId,
            String vehicleId,
            String customerName,
            String customerEmail,
            LocalDate startDate,
            LocalDate endDate) {

        RentalValidationData validationData =
                new RentalValidationData(
                        0,
                        false,
                        false
                );

        return rentVehicle(
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
     * Creates a rental after applying the selected vehicle type rules.
     *
     * @param rentalId unique rental identifier
     * @param vehicleId identifier of the selected vehicle
     * @param customerName customer name
     * @param customerEmail customer email address
     * @param startDate rental start date
     * @param endDate rental end date
     * @param validationData vehicle-specific validation information
     * @return newly created rental
     * @throws IllegalArgumentException if the rental information is invalid
     * @throws IllegalStateException if the vehicle is unavailable
     */
    public Rental rentVehicle(
            String rentalId,
            String vehicleId,
            String customerName,
            String customerEmail,
            LocalDate startDate,
            LocalDate endDate,
            RentalValidationData validationData) {

        validateRentalInformation(
                rentalId,
                vehicleId,
                customerName,
                customerEmail,
                validationData
        );

        validateRentalPeriod(
                startDate,
                endDate
        );

        String cleanedRentalId = rentalId.trim();
        String cleanedVehicleId = vehicleId.trim();

        if (rentalRepository.findById(cleanedRentalId) != null) {
            throw new IllegalArgumentException(
                    "Rental ID already exists."
            );
        }

        Vehicle vehicle = findVehicleById(
                cleanedVehicleId
        );

        vehicle.validateRental(validationData);

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new IllegalStateException(
                    "Vehicle is not available for rental."
            );
        }

        if (hasActiveRental(cleanedVehicleId)) {
            throw new IllegalStateException(
                    "Vehicle is already rented."
            );
        }

        Rental rental = new Rental(
                cleanedRentalId,
                vehicle,
                customerName.trim(),
                customerEmail.trim(),
                startDate,
                endDate,
                RentalStatus.ACTIVE
        );

        vehicle.setStatus(VehicleStatus.RENTED);

        vehicleRepository.save(vehicle);
        rentalRepository.save(rental);

        return rental;
    }

    /**
     * Returns a rented vehicle using the current date
     * supplied by the date provider.
     *
     * @param vehicleId identifier of the returned vehicle
     * @return closed rental record
     */
    public Rental returnVehicle(String vehicleId) {
        return returnVehicle(
                vehicleId,
                dateProvider.getCurrentDate()
        );
    }

    /**
     * Returns a rented vehicle using the supplied return date.
     *
     * @param vehicleId identifier of the returned vehicle
     * @param returnDate actual vehicle return date
     * @return closed and updated rental record
     * @throws IllegalArgumentException if the input is invalid
     */
    public Rental returnVehicle(
            String vehicleId,
            LocalDate returnDate) {

        if (rentalStrategy == null) {
            throw new IllegalArgumentException(
                    "No rental cost strategy is set."
            );
        }

        if (vehicleId == null
                || vehicleId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Vehicle ID cannot be empty."
            );
        }

        if (returnDate == null) {
            throw new IllegalArgumentException(
                    "Return date cannot be null."
            );
        }

        Rental rental = findRentalByVehicleId(
                vehicleId.trim()
        );

        if (returnDate.isBefore(rental.getStartDate())) {
            throw new IllegalArgumentException(
                    "Return date cannot be before the rental start date."
            );
        }

        double totalCost = rentalStrategy.calculateCost(
                rental,
                returnDate
        );

        rental.setTotalCost(totalCost);
        rental.closeRental();

        rental.getVehicle().setStatus(
                VehicleStatus.AVAILABLE
        );

        vehicleRepository.save(
                rental.getVehicle()
        );

        rentalRepository.update(rental);

        return rental;
    }

    /**
     * Finds a vehicle using its identifier.
     *
     * @param vehicleId vehicle identifier
     * @return matching vehicle
     * @throws IllegalArgumentException if the vehicle does not exist
     */
    private Vehicle findVehicleById(String vehicleId) {
        List<Vehicle> vehicles =
                vehicleRepository.findAll();

        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId()
                    .equalsIgnoreCase(vehicleId)) {

                return vehicle;
            }
        }

        throw new IllegalArgumentException(
                "Vehicle not found."
        );
    }

    /**
     * Checks whether the selected vehicle has an active rental.
     *
     * @param vehicleId vehicle identifier
     * @return true if an active rental exists; otherwise false
     */
    private boolean hasActiveRental(String vehicleId) {
        List<Rental> rentals =
                rentalRepository.findAll();

        for (Rental rental : rentals) {
            boolean sameVehicle =
                    rental.getVehicle()
                            .getId()
                            .equalsIgnoreCase(vehicleId);

            boolean activeRental =
                    rental.getStatus()
                            == RentalStatus.ACTIVE;

            if (sameVehicle && activeRental) {
                return true;
            }
        }

        return false;
    }

    /**
     * Finds the active rental associated with a vehicle.
     *
     * @param vehicleId vehicle identifier
     * @return active rental associated with the vehicle
     * @throws IllegalArgumentException if no active rental exists
     */
    private Rental findRentalByVehicleId(
            String vehicleId) {

        List<Rental> rentals =
                rentalRepository.findAll();

        for (Rental rental : rentals) {
            boolean sameVehicle =
                    rental.getVehicle()
                            .getId()
                            .equalsIgnoreCase(vehicleId);

            boolean activeRental =
                    rental.getStatus()
                            == RentalStatus.ACTIVE;

            if (sameVehicle && activeRental) {
                return rental;
            }
        }

        throw new IllegalArgumentException(
                "Rental for vehicle not found."
        );
    }

    /**
     * Validates the required rental information.
     *
     * @param rentalId rental identifier
     * @param vehicleId vehicle identifier
     * @param customerName customer name
     * @param customerEmail customer email
     * @param validationData vehicle-specific validation information
     */
    private void validateRentalInformation(
            String rentalId,
            String vehicleId,
            String customerName,
            String customerEmail,
            RentalValidationData validationData) {

        if (rentalId == null
                || rentalId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Rental ID cannot be empty."
            );
        }

        if (vehicleId == null
                || vehicleId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Vehicle ID cannot be empty."
            );
        }

        if (customerName == null
                || customerName.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Customer name cannot be empty."
            );
        }

        if (customerEmail == null
                || customerEmail.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Customer email cannot be empty."
            );
        }

        if (validationData == null) {
            throw new IllegalArgumentException(
                    "Rental validation data cannot be null."
            );
        }
    }

    /**
     * Validates the rental start date, end date, and duration.
     *
     * @param startDate rental start date
     * @param endDate rental end date
     */
    private void validateRentalPeriod(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Rental dates cannot be null."
            );
        }

        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException(
                    "End date must be after start date."
            );
        }

        long rentalDays = ChronoUnit.DAYS.between(
                startDate,
                endDate
        );

        if (rentalDays > MAX_RENTAL_DAYS) {
            throw new IllegalArgumentException(
                    "Rental period cannot exceed "
                            + MAX_RENTAL_DAYS
                            + " days."
            );
        }
    }
}