package com.vrms.domain;

import java.time.LocalDate;

/**
 * Defines a strategy for calculating the total cost of a vehicle rental.
 *
 * <p>This interface is part of the Strategy design pattern. Different
 * implementations can provide different cost calculation rules without changing
 * the rental service logic.</p>
 *
 * <p>For example, one strategy may calculate only the basic rental cost, while
 * another strategy may include late return penalties.</p>
 */
public interface RentalCostStrategy {

    /**
     * Calculates the total cost of a rental based on the rental information and
     * the actual return date.
     *
     * <p>The calculation may include the rental duration, vehicle daily rate,
     * late return penalty, or any other pricing rule depending on the
     * implementation.</p>
     *
     * @param rental the rental record for which the cost will be calculated
     * @param returnDate the actual date when the vehicle is returned
     * @return the calculated total rental cost
     * @throws IllegalArgumentException if the rental or return date is invalid
     */
    double calculateCost(Rental rental, LocalDate returnDate);

}