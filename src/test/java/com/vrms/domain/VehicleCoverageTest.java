package com.vrms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class VehicleCoverageTest {

    @Test
    void vehicle_shouldReturnAllValuesAndReadableDescription() {
        Vehicle vehicle = new Vehicle("V1", "Toyota", "Corolla", 40.0, VehicleStatus.AVAILABLE);

        assertEquals("V1", vehicle.getId());
        assertEquals("Toyota", vehicle.getBrand());
        assertEquals("Corolla", vehicle.getModel());
        assertEquals(40.0, vehicle.getPricePerDay(), 0.001);
        assertEquals(VehicleStatus.AVAILABLE, vehicle.getStatus());
        assertEquals(VehicleType.CAR, vehicle.getType());
        assertEquals("V1 - CAR - Toyota Corolla - 40.0 per day", vehicle.toString());
    }

    @Test
    void vehicle_whenValidationStrategyIsNull_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new TestVehicle("V1", "Toyota", "Corolla", 40.0, VehicleStatus.AVAILABLE, null));

        assertEquals("Rental validation strategy cannot be null.", exception.getMessage());
    }

    @Test
    void truckValidation_whenDataIsNull_shouldThrowException() {
        TruckRentalValidationStrategy strategy = new TruckRentalValidationStrategy();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> strategy.validate(null));

        assertEquals("Rental validation data cannot be null.", exception.getMessage());
    }

    @Test
    void motorcycleValidation_whenDataIsNull_shouldThrowException() {
        MotorcycleRentalValidationStrategy strategy = new MotorcycleRentalValidationStrategy();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> strategy.validate(null));

        assertEquals("Rental validation data cannot be null.", exception.getMessage());
    }

    @Test
    void electricVehicleValidation_whenDataIsNull_shouldThrowException() {
        ElectricVehicleRentalValidationStrategy strategy = new ElectricVehicleRentalValidationStrategy();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> strategy.validate(null));

        assertEquals("Rental validation data cannot be null.", exception.getMessage());
    }

    private static class TestVehicle extends Vehicle {

        TestVehicle(String id, String brand, String model, double pricePerDay, VehicleStatus status, RentalValidationStrategy validationStrategy) {
            super(id, brand, model, pricePerDay, status, validationStrategy);
        }
    }
}