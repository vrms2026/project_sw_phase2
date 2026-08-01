package com.vrms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class DomainModelTest {

    @Test
    void vehicleSetStatus_shouldChangeVehicleStatus() {
        Vehicle vehicle = new Vehicle(
                "V1",
                "Toyota",
                "Corolla",
                40.0,
                VehicleStatus.AVAILABLE
        );

        vehicle.setStatus(VehicleStatus.RENTED);

        assertEquals(
                VehicleStatus.RENTED,
                vehicle.getStatus()
        );
    }

    @Test
    void closeRental_shouldChangeRentalStatusToClosed() {
        Vehicle vehicle = new Vehicle(
                "V1",
                "Toyota",
                "Corolla",
                40.0,
                VehicleStatus.RENTED
        );

        Rental rental = createRental(vehicle);

        rental.closeRental();

        assertEquals(
                RentalStatus.CLOSED,
                rental.getStatus()
        );
    }

    @Test
    void newRental_totalCostShouldInitiallyBeZero() {
        Vehicle vehicle = new Vehicle(
                "V1",
                "Toyota",
                "Corolla",
                40.0,
                VehicleStatus.RENTED
        );

        Rental rental = createRental(vehicle);

        assertEquals(
                0.0,
                rental.getTotalCost(),
                0.001
        );
    }

    @Test
    void setTotalCost_shouldChangeRentalTotalCost() {
        Vehicle vehicle = new Vehicle(
                "V1",
                "Toyota",
                "Corolla",
                40.0,
                VehicleStatus.RENTED
        );

        Rental rental = createRental(vehicle);

        rental.setTotalCost(200.0);

        assertEquals(
                200.0,
                rental.getTotalCost(),
                0.001
        );
    }

    private Rental createRental(Vehicle vehicle) {
        return new Rental(
                "R1",
                vehicle,
                "Haneen",
                "haneen@example.com",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15),
                RentalStatus.ACTIVE
        );
    }
}