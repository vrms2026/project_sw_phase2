package com.vrms.domain;

/**
 * Validates the rental requirements for trucks.
 *
 * <p>The customer must have a special truck license
 * before renting a truck.</p>
 */
public class TruckRentalValidationStrategy
        implements RentalValidationStrategy {

    /**
     * Validates truck rental information.
     *
     * @param validationData rental validation information
     * @throws IllegalArgumentException if the validation data is null
     *                                  or a special truck license
     *                                  is not provided
     */
    @Override
    public void validate(
            RentalValidationData validationData) {

        if (validationData == null) {
            throw new IllegalArgumentException(
                    "Rental validation data cannot be null."
            );
        }

        if (!validationData.hasSpecialTruckLicense()) {
            throw new IllegalArgumentException(
                    "A special truck license is required."
            );
        }
    }
}