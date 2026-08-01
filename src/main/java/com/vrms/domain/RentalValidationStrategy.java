package com.vrms.domain;

/**
 * Defines a strategy for validating vehicle rental requirements.
 *
 * <p>Different vehicle types can use different validation strategies
 * without changing the rental service.</p>
 */
public interface RentalValidationStrategy {

    /**
     * Validates the information required for a rental.
     *
     * @param validationData rental validation information
     * @throws IllegalArgumentException if a rental requirement
     *                                  is not satisfied
     */
    void validate(RentalValidationData validationData);
}