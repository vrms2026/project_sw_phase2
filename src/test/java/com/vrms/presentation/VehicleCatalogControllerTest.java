package com.vrms.presentation;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vrms.application.VehicleCatalogService;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;

public class VehicleCatalogControllerTest {

    private VehicleCatalogService vehicleCatalogService;
    private VehicleCatalogController controller;

    @BeforeEach
    public void setUp() {
        vehicleCatalogService =
                mock(VehicleCatalogService.class);

        controller =
                new VehicleCatalogController(vehicleCatalogService);
    }

    @Test
    public void viewAvailableVehicles_shouldReturnVehiclesFromService() {
        List<Vehicle> availableVehicles = Arrays.asList(
                new Vehicle(
                        "V1",
                        "Toyota",
                        "Corolla",
                        40.0,
                        VehicleStatus.AVAILABLE
                ),
                new Vehicle(
                        "V3",
                        "Honda",
                        "Civic",
                        45.0,
                        VehicleStatus.AVAILABLE
                )
        );

        when(
                vehicleCatalogService.getAvailableVehicles()
        ).thenReturn(availableVehicles);

        List<Vehicle> result =
                controller.viewAvailableVehicles();

        assertSame(availableVehicles, result);

        verify(
                vehicleCatalogService
        ).getAvailableVehicles();
    }

    @Test
    public void viewAvailableVehicles_whenNoVehiclesAvailable_shouldReturnEmptyList() {
        List<Vehicle> emptyVehicles =
                Collections.emptyList();

        when(
                vehicleCatalogService.getAvailableVehicles()
        ).thenReturn(emptyVehicles);

        List<Vehicle> result =
                controller.viewAvailableVehicles();

        assertSame(emptyVehicles, result);
        assertTrue(result.isEmpty());

        verify(
                vehicleCatalogService
        ).getAvailableVehicles();
    }
}