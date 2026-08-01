package com.vrms.persistence;

import java.util.List;

import com.vrms.domain.Vehicle;

/**
 * Defines the operations required to store and retrieve vehicles.
 */
public interface VehicleRepository {

    /**
     * Returns all stored vehicles.
     *
     * @return a list containing all vehicles
     */
    List<Vehicle> findAll();

    /**
     * Finds a vehicle by its unique identifier.
     *
     * @param id the vehicle identifier
     * @return the matching vehicle, or null if it is not found
     */
    Vehicle findById(String id);

    /**
     * Saves a vehicle or its updated information.
     *
     * @param vehicle the vehicle to save
     */
    void save(Vehicle vehicle);
}