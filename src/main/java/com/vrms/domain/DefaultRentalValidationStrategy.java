package com.vrms.domain;

/**
 * Applies the default rental validation rules.
 *
 * <p>This strategy is used for vehicle types that do not require
 * additional rental conditions, such as cars and vans.</p>
 */
public class DefaultRentalValidationStrategy
        implements RentalValidationStrategy {

    /**
     * Validates the supplied rental information.
     *
     * @param validationData rental validation information
     * @throws IllegalArgumentException if the validation data is null
     */
    @Override
    public void validate(
            RentalValidationData validationData) {

        if (validationData == null) {
            throw new IllegalArgumentException(
                    "Rental validation data cannot be null."
            );
        }
    }
}