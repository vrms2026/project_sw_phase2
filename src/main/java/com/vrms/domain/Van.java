package com.vrms.domain;

/**
 * Represents a van available for rental.
 *
 * <p>Vans use the default rental validation strategy because
 * they do not require additional type-specific conditions.</p>
 */
public class Van extends Vehicle {

    /**
     * Creates a van with the provided information.
     *
     * @param id unique vehicle identifier
     * @param brand van brand
     * @param model van model
     * @param pricePerDay daily rental price
     * @param status current availability status
     */
    public Van(
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
     * @return {@link VehicleType#VAN}
     */
    @Override
    public VehicleType getType() {
        return VehicleType.VAN;
    }
}