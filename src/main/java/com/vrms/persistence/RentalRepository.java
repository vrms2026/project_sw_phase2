package com.vrms.persistence;

import java.util.List;

import com.vrms.domain.Rental;

/**
 * Defines the operations required to store and retrieve rental records.
 */
public interface RentalRepository {

    /**
     * Saves a new rental record.
     *
     * @param rental the rental record to save
     */
    void save(Rental rental);

    /**
     * Returns all stored rental records.
     *
     * @return a list containing all rentals
     */
    List<Rental> findAll();

    /**
     * Finds a rental by its unique identifier.
     *
     * @param rentalId the rental identifier
     * @return the matching rental, or null if it is not found
     */
    Rental findById(String rentalId);

    /**
     * Updates an existing rental record.
     *
     * @param rental the rental record containing the updated information
     */
    void update(Rental rental);
}