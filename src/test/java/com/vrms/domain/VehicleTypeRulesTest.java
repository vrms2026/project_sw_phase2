package com.vrms.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VehicleTypeRulesTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
    public void car_shouldReturnCarType() {
        Vehicle vehicle = new Car("V1", "Toyota", "Corolla", 40.0, VehicleStatus.AVAILABLE);
        assertEquals(VehicleType.CAR, vehicle.getType());
    }

    @Test
    public void van_shouldReturnVanType() {
        Vehicle vehicle = new Van("V2", "Ford", "Transit", 70.0, VehicleStatus.AVAILABLE);
        assertEquals(VehicleType.VAN, vehicle.getType());
    }

    @Test
    public void motorcycle_shouldReturnMotorcycleType() {
        Vehicle vehicle = new Motorcycle("V3", "Honda", "CBR", 35.0, VehicleStatus.AVAILABLE);
        assertEquals(VehicleType.MOTORCYCLE, vehicle.getType());
    }

    @Test
    public void motorcycle_whenCustomerIsUnderAge_shouldThrowException() {
        Vehicle motorcycle = new Motorcycle("V3", "Honda", "CBR", 35.0, VehicleStatus.AVAILABLE);
        RentalValidationData data = new RentalValidationData(20, false, false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> motorcycle.validateRental(data)
        );

        assertEquals("Customer must be at least 21 years old to rent a motorcycle.", exception.getMessage());
    }

    @Test
    public void motorcycle_whenCustomerAgeIsValid_shouldPass() {
        Vehicle motorcycle = new Motorcycle("V3", "Honda", "CBR", 35.0, VehicleStatus.AVAILABLE);
        RentalValidationData data = new RentalValidationData(21, false, false);

        assertDoesNotThrow(() -> motorcycle.validateRental(data));
    }

    @Test
    public void truck_shouldReturnTruckType() {
        Vehicle vehicle = new Truck("V4", "Volvo", "FH", 120.0, VehicleStatus.AVAILABLE);
        assertEquals(VehicleType.TRUCK, vehicle.getType());
    }

    @Test
    public void truck_withoutSpecialLicense_shouldThrowException() {
        Vehicle truck = new Truck("V4", "Volvo", "FH", 120.0, VehicleStatus.AVAILABLE);
        RentalValidationData data = new RentalValidationData(30, false, false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> truck.validateRental(data)
        );

        assertEquals("A special truck license is required.", exception.getMessage());
    }

    @Test
    public void truck_withSpecialLicense_shouldPass() {
        Vehicle truck = new Truck("V4", "Volvo", "FH", 120.0, VehicleStatus.AVAILABLE);
        RentalValidationData data = new RentalValidationData(30, true, false);

        assertDoesNotThrow(() -> truck.validateRental(data));
    }

    @Test
    public void electricVehicle_shouldReturnElectricVehicleType() {
        Vehicle vehicle = new ElectricVehicle("V5", "Tesla", "Model3", 90.0, VehicleStatus.AVAILABLE);
        assertEquals(VehicleType.ELECTRIC_VEHICLE, vehicle.getType());
    }

    @Test
    public void electricVehicle_withoutBatteryCheck_shouldThrowException() {
        Vehicle electricVehicle = new ElectricVehicle("V5", "Tesla", "Model3", 90.0, VehicleStatus.AVAILABLE);
        RentalValidationData data = new RentalValidationData(25, false, false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> electricVehicle.validateRental(data)
        );

        assertEquals("Battery check is required before renting an electric vehicle.", exception.getMessage());
    }

    @Test
    public void electricVehicle_afterBatteryCheck_shouldPass() {
        Vehicle electricVehicle = new ElectricVehicle("V5", "Tesla", "Model3", 90.0, VehicleStatus.AVAILABLE);
        RentalValidationData data = new RentalValidationData(25, false, true);

        assertDoesNotThrow(() -> electricVehicle.validateRental(data));
    }

    @Test
    public void validationData_whenAgeIsNegative_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new RentalValidationData(-1, false, false)
        );

        assertEquals("Customer age cannot be negative.", exception.getMessage());
    }

    @Test
    public void validateRental_whenValidationDataIsNull_shouldThrowException() {
        Vehicle vehicle = new Car("V1", "Toyota", "Corolla", 40.0, VehicleStatus.AVAILABLE);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> vehicle.validateRental(null)
        );

        assertEquals("Rental validation data cannot be null.", exception.getMessage());
    }

}
