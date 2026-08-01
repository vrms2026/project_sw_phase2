package com.vrms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.FileManagerRepository;
import com.vrms.persistence.FileVehicleRepository;
import com.vrms.persistence.ManagerRepository;
import com.vrms.persistence.VehicleRepository;

class VehicleCatalogServiceTest {

    @TempDir
    Path tempDir;

    private AuthService authService;
    private VehicleCatalogService vehicleCatalogService;

    @BeforeEach
    void setUp() throws IOException {
        Path managersFile = tempDir.resolve("managers.txt");
        Path vehiclesFile = tempDir.resolve("vehicles.txt");

        Files.write(
                managersFile,
                Arrays.asList("admin,1234"),
                StandardCharsets.UTF_8
        );

        Files.write(
                vehiclesFile,
                Arrays.asList(
                        "V1,Toyota,Corolla,40.0,AVAILABLE",
                        "V2,Kia,Sportage,60.0,RENTED",
                        "V3,Honda,Civic,45.0,AVAILABLE",
                        "V4,Hyundai,Tucson,55.0,RENTED"
                ),
                StandardCharsets.UTF_8
        );

        ManagerRepository managerRepository =
                new FileManagerRepository(managersFile);

        VehicleRepository vehicleRepository =
                new FileVehicleRepository(vehiclesFile);

        authService = new AuthService(managerRepository);

        vehicleCatalogService = new VehicleCatalogService(
                vehicleRepository,
                authService
        );
    }

    @Test
    void getAvailableVehicles_loggedIn_returnsOnlyAvailableVehicles() {
        authService.login("admin", "1234");

        List<Vehicle> vehicles =
                vehicleCatalogService.getAvailableVehicles();

        assertEquals(2, vehicles.size());

        assertEquals("V1", vehicles.get(0).getId());
        assertEquals(VehicleStatus.AVAILABLE, vehicles.get(0).getStatus());

        assertEquals("V3", vehicles.get(1).getId());
        assertEquals(VehicleStatus.AVAILABLE, vehicles.get(1).getStatus());
    }

    @Test
    void getAvailableVehicles_notLoggedIn_throwsException() {
        assertThrows(
                IllegalStateException.class,
                () -> vehicleCatalogService.getAvailableVehicles()
        );
    }

    @Test
    void getAvailableVehicles_afterLogout_throwsException() {
        authService.login("admin", "1234");
        authService.logout();

        assertThrows(
                IllegalStateException.class,
                () -> vehicleCatalogService.getAvailableVehicles()
        );
    }
}