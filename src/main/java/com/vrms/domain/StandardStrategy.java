package com.vrms.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Standard rental cost calculation strategy.
 *
 * <p>This class implements the {@link RentalCostStrategy} interface and provides
 * the default way to calculate the total rental cost for a returned vehicle.</p>
 *
 * <p>The strategy calculates the basic rental cost using the vehicle price per day
 * and rental duration. It also adds a fixed late return cost when the vehicle is
 * returned after the expected return date.</p>
 */
public class StandardStrategy implements RentalCostStrategy {

    /**
     * Fixed late return cost applied per late day.
     */
    private static final double LATE_COST_PER_DAY = 20.0;

    /**
     * Calculates the total rental cost for a rental.
     *
     * <p>The calculation is based on the vehicle price per day and the planned
     * rental duration. If the vehicle is returned late, an additional late cost is
     * added for each late day.</p>
     *
     * @param rental the rental record that contains vehicle and rental date information
     * @param returnDate the actual date when the vehicle is returned
     * @return the total calculated rental cost
     * @throws IllegalArgumentException if rental or return date is null
     */
    @Override
    public double calculateCost(Rental rental, LocalDate returnDate) {
        if (rental == null) {
            throw new IllegalArgumentException("Rental cannot be null.");
        }

        if (returnDate == null) {
            throw new IllegalArgumentException("Return date cannot be null.");
        }

        if (returnDate.isBefore(rental.getStartDate())) {
            throw new IllegalArgumentException("Return date cannot be before rental start date.");
        }

        LocalDate billedEndDate = returnDate.isBefore(rental.getEndDate()) ? returnDate : rental.getEndDate();
        long rentalDays = Math.max(1, ChronoUnit.DAYS.between(rental.getStartDate(), billedEndDate));
        double totalCost = rentalDays * rental.getVehicle().getPricePerDay();

        if (returnDate.isAfter(rental.getEndDate())) {
            long lateDays = ChronoUnit.DAYS.between(rental.getEndDate(), returnDate);
            totalCost += lateDays * LATE_COST_PER_DAY;
        }

        return totalCost;
    }
}