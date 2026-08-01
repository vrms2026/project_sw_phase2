package com.vrms.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vrms.domain.Car;
import com.vrms.domain.ElectricVehicle;
import com.vrms.domain.Motorcycle;
import com.vrms.domain.Truck;
import com.vrms.domain.Van;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;

public class FileVehicleRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    public void findAll_shouldCreateCorrectVehicleTypes()
            throws IOException {

        Path file = tempDir.resolve("vehicles.txt");

        Files.write(
                file,
                Arrays.asList(
                        "V1,CAR,Toyota,Corolla,40.0,AVAILABLE",
                        "V2,MOTORCYCLE,Honda,CBR,35.0,AVAILABLE",
                        "V3,VAN,Ford,Transit,70.0,AVAILABLE",
                        "V4,TRUCK,Volvo,FH,120.0,AVAILABLE",
                        "V5,ELECTRIC_VEHICLE,Tesla,Model3,90.0,AVAILABLE"
                ),
                StandardCharsets.UTF_8
        );

        FileVehicleRepository repository =
                new FileVehicleRepository(file);

        List<Vehicle> vehicles = repository.findAll();

        assertEquals(5, vehicles.size());
        assertInstanceOf(Car.class, repository.findById("V1"));
        assertInstanceOf(Motorcycle.class, repository.findById("V2"));
        assertInstanceOf(Van.class, repository.findById("V3"));
        assertInstanceOf(Truck.class, repository.findById("V4"));
        assertInstanceOf(
                ElectricVehicle.class,
                repository.findById("V5")
        );
    }

    @Test
    public void save_shouldKeepVehicleTypeAndStatus() {
        Path file = tempDir.resolve("vehicles.txt");

        FileVehicleRepository repository =
                new FileVehicleRepository(file);

        Vehicle truck = repository.findById("V4");
        truck.setStatus(VehicleStatus.RENTED);

        repository.save(truck);

        Vehicle savedTruck = repository.findById("V4");

        assertInstanceOf(Truck.class, savedTruck);

        assertEquals(
                VehicleStatus.RENTED,
                savedTruck.getStatus()
        );
    }

    @Test
    public void save_whenVehicleDoesNotExist_shouldAddVehicle() {
        Path file = tempDir.resolve("vehicles.txt");

        FileVehicleRepository repository =
                new FileVehicleRepository(file);

        Vehicle car = new Car(
                "V6",
                "BMW",
                "X5",
                100.0,
                VehicleStatus.AVAILABLE
        );

        repository.save(car);

        Vehicle savedVehicle = repository.findById("V6");

        assertInstanceOf(Car.class, savedVehicle);
        assertEquals("BMW", savedVehicle.getBrand());
        assertEquals("X5", savedVehicle.getModel());

        assertEquals(
                100.0,
                savedVehicle.getPricePerDay(),
                0.001
        );

        assertEquals(
                VehicleStatus.AVAILABLE,
                savedVehicle.getStatus()
        );
    }

    @Test
    public void oldFiveColumnFormat_shouldLoadAsCar()
            throws IOException {

        Path file = tempDir.resolve("vehicles.txt");

        Files.write(
                file,
                Arrays.asList(
                        "V1,Toyota,Corolla,40.0,AVAILABLE"
                ),
                StandardCharsets.UTF_8
        );

        FileVehicleRepository repository =
                new FileVehicleRepository(file);

        Vehicle vehicle = repository.findById("V1");

        assertInstanceOf(Car.class, vehicle);

        assertEquals(
                VehicleStatus.AVAILABLE,
                vehicle.getStatus()
        );
    }

    @Test
    public void electricShortType_shouldLoadAsElectricVehicle()
            throws IOException {

        Path file = tempDir.resolve("vehicles.txt");

        Files.write(
                file,
                Arrays.asList(
                        "V1,ELECTRIC,Tesla,ModelY,95.0,AVAILABLE"
                ),
                StandardCharsets.UTF_8
        );

        FileVehicleRepository repository =
                new FileVehicleRepository(file);

        assertInstanceOf(
                ElectricVehicle.class,
                repository.findById("V1")
        );
    }

    @Test
    public void findById_whenVehicleDoesNotExist_shouldReturnNull() {
        Path file = tempDir.resolve("vehicles.txt");

        FileVehicleRepository repository =
                new FileVehicleRepository(file);

        assertNull(repository.findById("V99"));
    }
}