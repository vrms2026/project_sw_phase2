package com.vrms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vrms.domain.Rental;
import com.vrms.domain.RentalStatus;
import com.vrms.domain.RentalValidationData;
import com.vrms.domain.VehicleStatus;
import com.vrms.domain.VehicleType;
import com.vrms.persistence.FileRentalRepository;
import com.vrms.persistence.FileVehicleRepository;

public class RentalServiceVehicleTypeTest {

    @TempDir
    Path tempDir;

    private FileVehicleRepository vehicleRepository;
    private FileRentalRepository rentalRepository;
    private RentalService rentalService;

    @BeforeEach
    public void setUp() {
        vehicleRepository = new FileVehicleRepository(tempDir.resolve("vehicles.txt"));
        rentalRepository = new FileRentalRepository(tempDir.resolve("rentals.txt"), vehicleRepository);
        rentalService = new RentalService(vehicleRepository, rentalRepository);
    }

    @Test
    public void rentMotorcycle_whenCustomerIsUnderAge_shouldThrowException() {
        RentalValidationData data = new RentalValidationData(20, false, false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> rentalService.rentVehicle(
                        "R20",
                        "V2",
                        "Ahmad",
                        "ahmad@example.com",
                        LocalDate.of(2026, 7, 10),
                        LocalDate.of(2026, 7, 15),
                        data
                )
        );

        assertEquals("Customer must be at least 21 years old to rent a motorcycle.", exception.getMessage());
        assertEquals(VehicleStatus.AVAILABLE, vehicleRepository.findById("V2").getStatus());
        assertEquals(0, rentalRepository.findAll().size());
    }

    @Test
    public void rentMotorcycle_whenCustomerAgeIsValid_shouldCreateRental() {
        RentalValidationData data = new RentalValidationData(21, false, false);

        Rental rental = rentalService.rentVehicle(
                "R21",
                "V2",
                "Ahmad",
                "ahmad@example.com",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15),
                data
        );

        assertEquals(RentalStatus.ACTIVE, rental.getStatus());
        assertEquals(VehicleType.MOTORCYCLE, rental.getVehicle().getType());
        assertEquals(VehicleStatus.RENTED, vehicleRepository.findById("V2").getStatus());
        assertEquals(1, rentalRepository.findAll().size());
    }

    @Test
    public void rentTruck_withoutSpecialLicense_shouldThrowException() {
        RentalValidationData data = new RentalValidationData(30, false, false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> rentalService.rentVehicle(
                        "R22",
                        "V4",
                        "Omar",
                        "omar@example.com",
                        LocalDate.of(2026, 7, 10),
                        LocalDate.of(2026, 7, 15),
                        data
                )
        );

        assertEquals("A special truck license is required.", exception.getMessage());
        assertEquals(VehicleStatus.AVAILABLE, vehicleRepository.findById("V4").getStatus());
        assertEquals(0, rentalRepository.findAll().size());
    }

    @Test
    public void rentTruck_withSpecialLicense_shouldCreateRental() {
        RentalValidationData data = new RentalValidationData(30, true, false);

        Rental rental = rentalService.rentVehicle(
                "R23",
                "V4",
                "Omar",
                "omar@example.com",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15),
                data
        );

        assertEquals(RentalStatus.ACTIVE, rental.getStatus());
        assertEquals(VehicleType.TRUCK, rental.getVehicle().getType());
        assertEquals(VehicleStatus.RENTED, vehicleRepository.findById("V4").getStatus());
        assertEquals(1, rentalRepository.findAll().size());
    }

    @Test
    public void rentElectricVehicle_withoutBatteryCheck_shouldThrowException() {
        RentalValidationData data = new RentalValidationData(25, false, false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> rentalService.rentVehicle(
                        "R24",
                        "V5",
                        "Sara",
                        "sara@example.com",
                        LocalDate.of(2026, 7, 10),
                        LocalDate.of(2026, 7, 15),
                        data
                )
        );

        assertEquals("Battery check is required before renting an electric vehicle.", exception.getMessage());
        assertEquals(VehicleStatus.AVAILABLE, vehicleRepository.findById("V5").getStatus());
        assertEquals(0, rentalRepository.findAll().size());
    }

    @Test
    public void rentElectricVehicle_afterBatteryCheck_shouldCreateRental() {
        RentalValidationData data = new RentalValidationData(25, false, true);

        Rental rental = rentalService.rentVehicle(
                "R25",
                "V5",
                "Sara",
                "sara@example.com",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15),
                data
        );

        assertEquals(RentalStatus.ACTIVE, rental.getStatus());
        assertEquals(VehicleType.ELECTRIC_VEHICLE, rental.getVehicle().getType());
        assertEquals(VehicleStatus.RENTED, vehicleRepository.findById("V5").getStatus());
        assertEquals(1, rentalRepository.findAll().size());
    }

    @Test
    public void rentCar_withDefaultValidation_shouldCreateRental() {
        Rental rental = rentalService.rentVehicle(
                "R26",
                "V1",
                "Lina",
                "lina@example.com",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15)
        );

        assertEquals(RentalStatus.ACTIVE, rental.getStatus());
        assertEquals(VehicleType.CAR, rental.getVehicle().getType());
        assertEquals(VehicleStatus.RENTED, vehicleRepository.findById("V1").getStatus());
        assertEquals(1, rentalRepository.findAll().size());
    }

    @Test
    public void rentVan_withDefaultValidation_shouldCreateRental() {
        Rental rental = rentalService.rentVehicle(
                "R27",
                "V3",
                "Dana",
                "dana@example.com",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15)
        );

        assertEquals(RentalStatus.ACTIVE, rental.getStatus());
        assertEquals(VehicleType.VAN, rental.getVehicle().getType());
        assertEquals(VehicleStatus.RENTED, vehicleRepository.findById("V3").getStatus());
        assertEquals(1, rentalRepository.findAll().size());
    }
}