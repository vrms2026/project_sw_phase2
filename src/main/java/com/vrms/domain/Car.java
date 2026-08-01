package com.vrms.domain;

/**
 * Represents a car available for rental.
 *
 * <p>Cars use the default rental validation strategy because
 * they do not require additional type-specific conditions.</p>
 */
public class Car extends Vehicle {

    /**
     * Creates a car with the provided information.
     *
     * @param id unique vehicle identifier
     * @param brand car brand
     * @param model car model
     * @param pricePerDay daily rental price
     * @param status current availability status
     */
    public Car(
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
                status
        );
    }

    /**
     * Returns the type of this vehicle.
     *
     * @return {@link VehicleType#CAR}
     */
    @Override
    public VehicleType getType() {
        return VehicleType.CAR;
    }
}