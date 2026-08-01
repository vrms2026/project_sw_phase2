package com.vrms.domain;

/**
 * Represents an electric vehicle available for rental.
 *
 * <p>Electric vehicles use a specific rental validation strategy
 * that requires a battery check before rental.</p>
 */
public class ElectricVehicle extends Vehicle {

    /**
     * Creates an electric vehicle with the provided information.
     *
     * @param id unique vehicle identifier
     * @param brand vehicle brand
     * @param model vehicle model
     * @param pricePerDay daily rental price
     * @param status current availability status
     */
    public ElectricVehicle(
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
                new ElectricVehicleRentalValidationStrategy()
        );
    }

    /**
     * Returns the type of this vehicle.
     *
     * @return {@link VehicleType#ELECTRIC_VEHICLE}
     */
    @Override
    public VehicleType getType() {
        return VehicleType.ELECTRIC_VEHICLE;
    }
}