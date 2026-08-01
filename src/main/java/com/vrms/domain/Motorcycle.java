package com.vrms.domain;

/**
 * Represents a motorcycle available for rental.
 *
 * <p>Motorcycles use a specific validation strategy that checks
 * the customer's minimum rental age.</p>
 */
public class Motorcycle extends Vehicle {

    /**
     * Creates a motorcycle with the provided information.
     *
     * @param id unique vehicle identifier
     * @param brand motorcycle brand
     * @param model motorcycle model
     * @param pricePerDay daily rental price
     * @param status current availability status
     */
    public Motorcycle(
            String id,
            String brand,
            String model,
            double pricePerDay,
            VehicleStatus status) {

        super(
                id,
                brand,
                model,
                pricePerDay,
                status,
                new MotorcycleRentalValidationStrategy()
        );
    }

    /**
     * Returns the type of this vehicle.
     *
     * @return {@link VehicleType#MOTORCYCLE}
     */
    @Override
    public VehicleType getType() {
        return VehicleType.MOTORCYCLE;
    }
}