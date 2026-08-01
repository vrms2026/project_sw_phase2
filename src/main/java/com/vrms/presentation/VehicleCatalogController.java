package com.vrms.presentation;

import java.util.List;
import java.util.Objects;

import com.vrms.application.VehicleCatalogService;
import com.vrms.domain.Vehicle;

/**
 * Handles requests to view the available vehicle catalog.
 *
 * <p>The controller delegates vehicle catalog operations to
 * {@link VehicleCatalogService}.</p>
 */
public class VehicleCatalogController {

    private final VehicleCatalogService vehicleCatalogService;

    public VehicleCatalogController(
            VehicleCatalogService vehicleCatalogService) {

        this.vehicleCatalogService = Objects.requireNonNull(
                vehicleCatalogService,
                "Vehicle catalog service cannot be null."
        );
    }
    public List<Vehicle> viewAvailableVehicles() {
        return vehicleCatalogService.getAvailableVehicles();
    }
}