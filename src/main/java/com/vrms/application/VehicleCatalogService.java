package com.vrms.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.VehicleRepository;

/**
 * Provides operations for viewing the available vehicle catalog.
 *
 * <p>Only an authenticated manager can view the available vehicles.</p>
 */
public class VehicleCatalogService {

    /**
     * Repository used to retrieve vehicle records.
     */
    private final VehicleRepository vehicleRepository;

    /**
     * Authentication service used to verify manager login.
     */
    private final AuthService authService;

    /**
     * Creates a vehicle catalog service.
     *
     * @param vehicleRepository repository containing vehicle records
     * @param authService service used to verify manager authentication
     * @throws NullPointerException if either dependency is null
     */
    public VehicleCatalogService(
            VehicleRepository vehicleRepository,
            AuthService authService) {

        this.vehicleRepository = Objects.requireNonNull(
                vehicleRepository,
                "Vehicle repository cannot be null."
        );

        this.authService = Objects.requireNonNull(
                authService,
                "Authentication service cannot be null."
        );
    }

    /**
     * Returns all vehicles currently available for rental.
     *
     * @return a list containing the available vehicles
     * @throws IllegalStateException if no manager is logged in
     */
    public List<Vehicle> getAvailableVehicles() {
        if (!authService.isLoggedIn()) {
            throw new IllegalStateException(
                    "Please login first."
            );
        }

        List<Vehicle> availableVehicles =
                new ArrayList<>();

        for (Vehicle vehicle
                : vehicleRepository.findAll()) {

            if (vehicle.getStatus()
                    == VehicleStatus.AVAILABLE) {

                availableVehicles.add(vehicle);
            }
        }

        return availableVehicles;
    }
}