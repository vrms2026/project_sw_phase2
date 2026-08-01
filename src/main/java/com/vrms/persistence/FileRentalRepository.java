package com.vrms.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.vrms.domain.Rental;
import com.vrms.domain.RentalStatus;
import com.vrms.domain.Vehicle;

/**
 * Stores and retrieves rental records using a text file.
 *
 * <p>Each rental is connected to a vehicle obtained through the
 * {@link VehicleRepository}.</p>
 */
public class FileRentalRepository implements RentalRepository {

    /**
     * The path of the file used to store rental records.
     */
    private final Path filePath;

    /**
     * Repository used to retrieve the vehicles associated with rentals.
     */
    private final VehicleRepository vehicleRepository;

    /**
     * Creates a rental repository using the default rental and vehicle files.
     */
    public FileRentalRepository() {
        this(
                Paths.get("data", "rentals.txt"),
                new FileVehicleRepository()
        );
    }

    /**
     * Creates a rental repository using the specified rental file.
     *
     * @param filePath the path of the rentals file
     */
    public FileRentalRepository(Path filePath) {
        this(
                filePath,
                new FileVehicleRepository()
        );
    }

    /**
     * Creates a rental repository using the default rental file and the
     * specified vehicle repository.
     *
     * @param vehicleRepository repository used to retrieve vehicles
     */
    public FileRentalRepository(
            VehicleRepository vehicleRepository) {

        this(
                Paths.get("data", "rentals.txt"),
                vehicleRepository
        );
    }

    /**
     * Creates a rental repository using the specified file and vehicle
     * repository.
     *
     * @param filePath the path of the rentals file
     * @param vehicleRepository repository used to retrieve vehicles
     * @throws IllegalArgumentException if the file path or repository is null
     */
    public FileRentalRepository(
            Path filePath,
            VehicleRepository vehicleRepository) {

        if (filePath == null) {
            throw new IllegalArgumentException(
                    "File path cannot be null."
            );
        }

        if (vehicleRepository == null) {
            throw new IllegalArgumentException(
                    "Vehicle repository cannot be null."
            );
        }

        this.filePath = filePath;
        this.vehicleRepository = vehicleRepository;

        createFile();
    }

    /**
     * Creates the rentals file and its parent directories when they do not
     * already exist.
     *
     * @throws RuntimeException if the file cannot be created
     */
    private void createFile() {
        try {
            Path parent = filePath.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException exception) {
            throw new RuntimeException(
                    "Could not create rentals file.",
                    exception
            );
        }
    }

    /**
     * Saves a new rental or replaces an existing rental with the same ID.
     *
     * @param rental the rental to save
     * @throws IllegalArgumentException if the rental is null or its ID is empty
     */
    @Override
    public void save(Rental rental) {
        if (rental == null) {
            throw new IllegalArgumentException(
                    "Rental cannot be null."
            );
        }

        String rentalId = rental.getRentalId();

        if (rentalId == null || rentalId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Rental ID cannot be empty."
            );
        }

        String searchedRentalId = rentalId.trim();

        List<Rental> rentals = findAll();
        boolean found = false;

        for (int i = 0; i < rentals.size(); i++) {
            Rental savedRental = rentals.get(i);

            if (savedRental.getRentalId()
                    .equalsIgnoreCase(searchedRentalId)) {

                rentals.set(i, rental);
                found = true;
                break;
            }
        }

        if (!found) {
            rentals.add(rental);
        }

        writeAll(rentals);
    }

    /**
     * Returns all valid rental records stored in the rentals file.
     *
     * <p>The method supports the old seven-field format and the newer
     * eight-field format that includes the total rental cost.</p>
     *
     * @return a list containing all stored rentals
     * @throws RuntimeException if the rentals file cannot be read
     */
    @Override
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(
                    filePath,
                    StandardCharsets.UTF_8
            );

            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",", -1);

                /*
                 * Old format:
                 * rentalId, customerName, customerEmail, vehicleId,
                 * startDate, endDate, status
                 *
                 * New format:
                 * rentalId, customerName, customerEmail, vehicleId,
                 * startDate, endDate, status, totalCost
                 */
                if (data.length != 7 && data.length != 8) {
                    continue;
                }

                Vehicle vehicle = vehicleRepository.findById(
                        data[3].trim()
                );

                if (vehicle == null) {
                    continue;
                }

                double totalCost = 0.0;

                if (data.length == 8
                        && !data[7].trim().isEmpty()) {

                    totalCost = Double.parseDouble(
                            data[7].trim()
                    );
                }

                Rental rental = new Rental(
                        data[0].trim(),
                        vehicle,
                        data[1].trim(),
                        data[2].trim(),
                        LocalDate.parse(data[4].trim()),
                        LocalDate.parse(data[5].trim()),
                        RentalStatus.valueOf(data[6].trim()),
                        totalCost
                );

                rentals.add(rental);
            }

            return rentals;
        } catch (IOException exception) {
            throw new RuntimeException(
                    "Could not read rentals file.",
                    exception
            );
        }
    }

    /**
     * Finds a rental using its unique identifier.
     *
     * @param rentalId the rental identifier
     * @return the matching rental, or null if it is not found
     */
    @Override
    public Rental findById(String rentalId) {
        if (rentalId == null || rentalId.trim().isEmpty()) {
            return null;
        }

        String searchedRentalId = rentalId.trim();

        for (Rental rental : findAll()) {
            if (rental.getRentalId()
                    .equalsIgnoreCase(searchedRentalId)) {

                return rental;
            }
        }

        return null;
    }

    /**
     * Updates an existing rental record.
     *
     * @param rental the rental containing the updated information
     */
    @Override
    public void update(Rental rental) {
        save(rental);
    }

    /**
     * Rewrites the rentals file using the provided rental records.
     *
     * @param rentals the rentals to write to the file
     * @throws RuntimeException if the rentals cannot be written
     */
    private void writeAll(List<Rental> rentals) {
        List<String> lines = new ArrayList<>();

        for (Rental rental : rentals) {
            String line =
                    rental.getRentalId()
                            + "," + rental.getCustomerName()
                            + "," + rental.getCustomerEmail()
                            + "," + rental.getVehicle().getId()
                            + "," + rental.getStartDate()
                            + "," + rental.getEndDate()
                            + "," + rental.getStatus()
                            + "," + rental.getTotalCost();

            lines.add(line);
        }

        try {
            Files.write(
                    filePath,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException exception) {
            throw new RuntimeException(
                    "Could not save rentals file.",
                    exception
            );
        }
    }
}