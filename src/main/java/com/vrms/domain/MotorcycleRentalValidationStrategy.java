package com.vrms.domain;

/**
 * Validates the rental requirements for motorcycles.
 *
 * <p>The customer must be at least 21 years old
 * to rent a motorcycle.</p>
 */
public class MotorcycleRentalValidationStrategy
        implements RentalValidationStrategy {

    /**
     * Minimum customer age required to rent a motorcycle.
     */
    private static final int MINIMUM_RENTAL_AGE = 21;

    /**
     * Validates motorcycle rental information.
     *
     * @param validationData rental validation information
     * @throws IllegalArgumentException if the validation data is null
     *                                  or the customer is under 21
     */
    @Override
    public void validate(
            RentalValidationData validationData) {

        if (validationData == null) {
            throw new IllegalArgumentException(
                    "Rental validation data cannot be null."
            );
        }

        if (validationData.getCustomerAge()
                < MINIMUM_RENTAL_AGE) {

            throw new IllegalArgumentException(
                    "Customer must be at least "
                            + MINIMUM_RENTAL_AGE
                            + " years old to rent a motorcycle."
            );
        }
    }
}