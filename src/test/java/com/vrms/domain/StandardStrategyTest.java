package com.vrms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StandardStrategyTest {

    private Vehicle vehicle;
    private StandardStrategy strategy;

    @BeforeEach
    public void setUp() {

        vehicle = new Vehicle(
                "V1",
                "Toyota",
                "Corolla",
                50.0,
                VehicleStatus.RENTED
        );

        strategy = new StandardStrategy();
    }

    @Test
    public void calculateCost_whenReturnedOnTime_shouldCalculateBasicCost() {

        Rental rental = createRental(
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5)
        );

        double cost = strategy.calculateCost(
                rental,
                LocalDate.of(2026, 7, 5)
        );

        // Four rental days multiplied by $50.
        assertEquals(200.0, cost, 0.001);
    }

    @Test
    public void calculateCost_whenReturnedLate_shouldAddLatePenalty() {

        Rental rental = createRental(
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5)
        );

        double cost = strategy.calculateCost(
                rental,
                LocalDate.of(2026, 7, 7)
        );

        /*
         * Basic cost: 4 × $50 = $200
         * Late penalty: 2 × $20 = $40
         * Total: $240
         */
        assertEquals(240.0, cost, 0.001);
    }

    @Test
    public void calculateCost_whenReturnedEarly_shouldChargeActualDuration() {

        Rental rental = createRental(
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5)
        );

        double cost = strategy.calculateCost(
                rental,
                LocalDate.of(2026, 7, 3)
        );

        assertEquals(100.0, cost, 0.001);
    }

    @Test
    public void calculateCost_whenRentalIsNull_shouldThrowException() {

        assertThrows(
                IllegalArgumentException.class,
                () -> strategy.calculateCost(
                        null,
                        LocalDate.of(2026, 7, 5)
                )
        );
    }

    @Test
    public void calculateCost_whenReturnDateIsNull_shouldThrowException() {

        Rental rental = createRental(
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> strategy.calculateCost(rental, null)
        );
    }

    private Rental createRental(
            LocalDate startDate,
            LocalDate endDate) {

        return new Rental(
                "R1",
                vehicle,
                "Customer",
                "customer@example.com",
                startDate,
                endDate,
                RentalStatus.ACTIVE
        );
    }
}