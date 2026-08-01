package com.vrms.domain;

/**
 * Validates the rental requirements for electric vehicles.
 *
 * <p>The vehicle battery must be checked before the rental
 * can be accepted.</p>
 */
public class ElectricVehicleRentalValidationStrategy
        implements RentalValidationStrategy {

    /**
     * Validates electric vehicle rental information.
     *
     * @param validationData rental validation information
     * @throws IllegalArgumentException if the validation data is null
     *                                  or the battery was not checked
     */
    @Override
    public void validate(
            RentalValidationData validationData) {

        if (validationData == null) {
            throw new IllegalArgumentException(
                    "Rental validation data cannot be null."
            );
        }

        if (!validationData.isBatteryChecked()) {
            throw new IllegalArgumentException(
                    "Battery check is required before renting an electric vehicle."
            );
        }
    }
}