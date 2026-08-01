package com.vrms.domain;

import java.time.LocalDate;

/**
 * Represents a rental record for a vehicle in the
 * Vehicle Rental Management System.
 */
public class Rental {

    /**
     * Unique identifier for the rental record.
     */
    private final String rentalId;

    /**
     * The vehicle rented by the customer.
     */
    private final Vehicle vehicle;

    /**
     * The name of the customer who rented the vehicle.
     */
    private final String customerName;

    /**
     * The email address of the customer.
     */
    private final String customerEmail;

    /**
     * The start date of the rental period.
     */
    private final LocalDate startDate;

    /**
     * The expected end date of the rental period.
     */
    private final LocalDate endDate;

    /**
     * The current rental status.
     */
    private RentalStatus status;

    /**
     * The total rental cost.
     */
    private double totalCost;

    /**
     * Creates a new rental with an initial total cost of zero.
     *
     * @param rentalId unique identifier for the rental
     * @param vehicle rented vehicle
     * @param customerName customer name
     * @param customerEmail customer email
     * @param startDate rental start date
     * @param endDate expected rental end date
     * @param status rental status
     */
    public Rental(
            String rentalId,
            Vehicle vehicle,
            String customerName,
            String customerEmail,
            LocalDate startDate,
            LocalDate endDate,
            RentalStatus status) {

        this(
                rentalId,
                vehicle,
                customerName,
                customerEmail,
                startDate,
                endDate,
                status,
                0.0
        );
    }

    /**
     * Creates a rental and restores its saved total cost.
     *
     * @param rentalId unique identifier for the rental
     * @param vehicle rented vehicle
     * @param customerName customer name
     * @param customerEmail customer email
     * @param startDate rental start date
     * @param endDate expected rental end date
     * @param status rental status
     * @param totalCost saved total rental cost
     */
    public Rental(
            String rentalId,
            Vehicle vehicle,
            String customerName,
            String customerEmail,
            LocalDate startDate,
            LocalDate endDate,
            RentalStatus status,
            double totalCost) {

        this.rentalId = rentalId;
        this.vehicle = vehicle;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.totalCost = totalCost;
    }

    /**
     * Returns the rental identifier.
     *
     * @return the rental identifier
     */
    public String getRentalId() {
        return rentalId;
    }

    /**
     * Returns the rented vehicle.
     *
     * @return the rented vehicle
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Returns the customer name.
     *
     * @return the customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Returns the customer email.
     *
     * @return the customer email
     */
    public String getCustomerEmail() {
        return customerEmail;
    }

    /**
     * Returns the rental start date.
     *
     * @return the rental start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the expected rental end date.
     *
     * @return the rental end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Returns the current rental status.
     *
     * @return the rental status
     */
    public RentalStatus getStatus() {
        return status;
    }

    /**
     * Returns the total rental cost.
     *
     * @return the total rental cost
     */
    public double getTotalCost() {
        return totalCost;
    }

    /**
     * Updates the total rental cost.
     *
     * @param totalCost the calculated rental cost
     */
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    /**
     * Closes the rental after the vehicle is returned.
     */
    public void closeRental() {
        this.status = RentalStatus.CLOSED;
    }
}