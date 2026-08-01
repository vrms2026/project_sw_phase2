package com.vrms.domain;

/**
 * Represents a general vehicle in the Vehicle Rental Management System.
 *
 * <p>This class contains the common information and behavior shared by
 * all vehicle types, such as cars, motorcycles, vans, trucks, and
 * electric vehicles.</p>
 */
public class Vehicle {

    /**
     * Unique identifier of the vehicle.
     */
    private final String id;

    /**
     * Manufacturer or brand of the vehicle.
     */
    private final String brand;

    /**
     * Model name of the vehicle.
     */
    private final String model;

    /**
     * Daily rental price of the vehicle.
     */
    private final double pricePerDay;

    /**
     * Current availability status of the vehicle.
     */
    private VehicleStatus status;

    /**
     * Strategy used to validate rental requirements.
     */
    private final RentalValidationStrategy validationStrategy;

    /**
     * Creates a vehicle using the default rental validation strategy.
     *
     * @param id unique vehicle identifier
     * @param brand vehicle brand
     * @param model vehicle model
     * @param pricePerDay daily rental price
     * @param status current vehicle status
     */
    public Vehicle(
            String id,
            String brand,
            String model,
            double pricePerDay,
            VehicleStatus status) {

        this(
                id,
                brand,
                model,
                pricePerDay,
                status,
                new DefaultRentalValidationStrategy()
        );
    }

    /**
     * Creates a vehicle using a specific rental validation strategy.
     *
     * @param id unique vehicle identifier
     * @param brand vehicle brand
     * @param model vehicle model
     * @param pricePerDay daily rental price
     * @param status current vehicle status
     * @param validationStrategy strategy used to validate rental requests
     * @throws IllegalArgumentException if the validation strategy is null
     */
    protected Vehicle(
            String id,
            String brand,
            String model,
            double pricePerDay,
            VehicleStatus status,
            RentalValidationStrategy validationStrategy) {

        if (validationStrategy == null) {
            throw new IllegalArgumentException(
                    "Rental validation strategy cannot be null."
            );
        }

        this.id = id;
        this.brand = brand;
        this.model = model;
        this.pricePerDay = pricePerDay;
        this.status = status;
        this.validationStrategy = validationStrategy;
    }

    /**
     * Returns the vehicle identifier.
     *
     * @return the vehicle identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the vehicle brand.
     *
     * @return the vehicle brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Returns the vehicle model.
     *
     * @return the vehicle model
     */
    public String getModel() {
        return model;
    }

    /**
     * Returns the daily rental price.
     *
     * @return the daily rental price
     */
    public double getPricePerDay() {
        return pricePerDay;
    }

    /**
     * Returns the current vehicle status.
     *
     * @return the current vehicle status
     */
    public VehicleStatus getStatus() {
        return status;
    }

    /**
     * Changes the current vehicle status.
     *
     * @param status the new vehicle status
     */
    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    /**
     * Returns the type of the vehicle.
     *
     * <p>Subclasses override this method to return their specific type.</p>
     *
     * @return the vehicle type
     */
    public VehicleType getType() {
        return VehicleType.CAR;
    }

    /**
     * Validates the rental request using the assigned strategy.
     *
     * @param validationData information used during rental validation
     * @throws IllegalArgumentException if the rental requirements
     *                                  are not satisfied
     */
    public void validateRental(
            RentalValidationData validationData) {

        validationStrategy.validate(validationData);
    }

    /**
     * Returns a readable description of the vehicle.
     *
     * @return vehicle information as text
     */
    @Override
    public String toString() {
        return id
                + " - " + getType()
                + " - " + brand
                + " " + model
                + " - " + pricePerDay
                + " per day";
    }
}