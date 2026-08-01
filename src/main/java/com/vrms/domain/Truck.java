package com.vrms.domain;

/**
 * Represents a truck available for rental.
 *
 * <p>Trucks use a specific validation strategy that requires
 * a special truck license.</p>
 */
public class Truck extends Vehicle {

    /**
     * Creates a truck with the provided information.
     *
     * @param id unique vehicle identifier
     * @param brand truck brand
     * @param model truck model
     * @param pricePerDay daily rental price
     * @param status current availability status
     */
    public Truck(
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
                new TruckRentalValidationStrategy()
        );
    }

    /**
     * Returns the type of this vehicle.
     *
     * @return {@link VehicleType#TRUCK}
     */
    @Override
    public VehicleType getType() {
        return VehicleType.TRUCK;
    }
}