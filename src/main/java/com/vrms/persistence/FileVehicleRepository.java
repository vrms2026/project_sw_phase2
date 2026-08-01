package com.vrms.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.vrms.domain.Car;
import com.vrms.domain.ElectricVehicle;
import com.vrms.domain.Motorcycle;
import com.vrms.domain.Truck;
import com.vrms.domain.Van;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.domain.VehicleType;

/**
 * Stores and retrieves vehicle information using a text file.
 *
 * <p>Each vehicle is stored on a separate line with its identifier,
 * type, brand, model, daily rental price, and current status.</p>
 */
public class FileVehicleRepository implements VehicleRepository {

    /**
     * The path of the file used to store vehicle information.
     */
    private final Path filePath;

    /**
     * Creates a vehicle repository using the default vehicles file.
     */
    public FileVehicleRepository() {
        this(Paths.get("data", "vehicles.txt"));
    }

    /**
     * Creates a vehicle repository using the specified file path.
     *
     * @param filePath the path of the vehicles file
     * @throws IllegalArgumentException if the file path is null
     */
    public FileVehicleRepository(Path filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException(
                    "File path cannot be null."
            );
        }

        this.filePath = filePath;
        createFile();
    }

    /**
     * Creates the vehicles file and its parent directories when they
     * do not already exist.
     *
     * <p>If the file is empty, default vehicle records of different
     * vehicle types are added.</p>
     *
     * @throws RuntimeException if the vehicles file cannot be created
     *                          or initialized
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

            String content = new String(
                    Files.readAllBytes(filePath),
                    StandardCharsets.UTF_8
            ).trim();

            if (content.isEmpty()) {
                Files.write(
                        filePath,
                        Arrays.asList(
                                "V1,CAR,Toyota,Corolla,40.0,AVAILABLE",
                                "V2,MOTORCYCLE,Honda,CBR,35.0,AVAILABLE",
                                "V3,VAN,Ford,Transit,70.0,AVAILABLE",
                                "V4,TRUCK,Volvo,FH,120.0,AVAILABLE",
                                "V5,ELECTRIC_VEHICLE,Tesla,Model3,90.0,AVAILABLE"
                        ),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING
                );
            }
        } catch (IOException exception) {
            throw new RuntimeException(
                    "Could not create vehicles file.",
                    exception
            );
        }
    }

    /**
     * Returns all valid vehicles stored in the vehicles file.
     *
     * <p>The method supports both the old five-field format and the
     * new six-field format that includes the vehicle type.</p>
     *
     * @return a list containing all stored vehicles
     * @throws RuntimeException if the vehicles file cannot be read
     */
    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = new ArrayList<>();

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
                Vehicle vehicle;
                if (data.length == 5) {
                    vehicle = createVehicle(
                            VehicleType.CAR,
                            data[0].trim(),
                            data[1].trim(),
                            data[2].trim(),
                            Double.parseDouble(data[3].trim()),
                            VehicleStatus.valueOf(
                                    data[4].trim()
                            )
                    );

                } else if (data.length == 6) {
                    vehicle = createVehicle(
                            parseType(data[1]),
                            data[0].trim(),
                            data[2].trim(),
                            data[3].trim(),
                            Double.parseDouble(data[4].trim()),
                            VehicleStatus.valueOf(
                                    data[5].trim()
                            )
                    );

                } else {
                    continue;
                }

                vehicles.add(vehicle);
            }

            return vehicles;
        } catch (IOException exception) {
            throw new RuntimeException(
                    "Could not read vehicles file.",
                    exception
            );
        }
    }

    /**
     * Converts a stored vehicle type value into a {@link VehicleType}.
     *
     * <p>The value ELECTRIC is also accepted as an alias for
     * ELECTRIC_VEHICLE.</p>
     *
     * @param value the stored vehicle type
     * @return the corresponding vehicle type
     */
    private VehicleType parseType(String value) {
        String type = value
                .trim()
                .toUpperCase(Locale.ROOT);

        if ("ELECTRIC".equals(type)) {
            return VehicleType.ELECTRIC_VEHICLE;
        }

        return VehicleType.valueOf(type);
    }

    /**
     * Creates the correct vehicle subclass according to its type.
     *
     * @param type vehicle type
     * @param id vehicle identifier
     * @param brand vehicle brand
     * @param model vehicle model
     * @param pricePerDay daily rental price
     * @param status current vehicle status
     * @return a vehicle object of the appropriate subclass
     */
    private Vehicle createVehicle(
            VehicleType type,
            String id,
            String brand,
            String model,
            double pricePerDay,
            VehicleStatus status) {

        switch (type) {
            case MOTORCYCLE:
                return new Motorcycle(
                        id,
                        brand,
                        model,
                        pricePerDay,
                        status
                );

            case VAN:
                return new Van(
                        id,
                        brand,
                        model,
                        pricePerDay,
                        status
                );

            case TRUCK:
                return new Truck(
                        id,
                        brand,
                        model,
                        pricePerDay,
                        status
                );

            case ELECTRIC_VEHICLE:
                return new ElectricVehicle(
                        id,
                        brand,
                        model,
                        pricePerDay,
                        status
                );

            case CAR:
            default:
                return new Car(
                        id,
                        brand,
                        model,
                        pricePerDay,
                        status
                );
        }
    }

    /**
     * Finds a vehicle using its unique identifier.
     *
     * @param id the vehicle identifier
     * @return the matching vehicle, or null if it is not found
     */
    @Override
    public Vehicle findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        String searchedId = id.trim();

        for (Vehicle vehicle : findAll()) {
            if (vehicle.getId()
                    .equalsIgnoreCase(searchedId)) {

                return vehicle;
            }
        }

        return null;
    }

    /**
     * Saves a new vehicle or updates an existing vehicle with the same ID.
     *
     * @param vehicle the vehicle to save
     * @throws IllegalArgumentException if the vehicle is null
     *                                  or its ID is empty
     */
    @Override
    public void save(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException(
                    "Vehicle cannot be null."
            );
        }

        String vehicleId = vehicle.getId();

        if (vehicleId == null || vehicleId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Vehicle ID cannot be empty."
            );
        }

        String searchedId = vehicleId.trim();

        List<Vehicle> vehicles = findAll();
        boolean found = false;

        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle savedVehicle = vehicles.get(i);

            if (savedVehicle.getId()
                    .equalsIgnoreCase(searchedId)) {

                vehicles.set(i, vehicle);
                found = true;
                break;
            }
        }

        if (!found) {
            vehicles.add(vehicle);
        }

        writeAll(vehicles);
    }

    /**
     * Rewrites the vehicles file using the provided vehicles.
     *
     * <p>The vehicle type is stored to preserve the correct subclass when
     * the vehicle is loaded again.</p>
     *
     * @param vehicles the vehicles to write to the file
     * @throws RuntimeException if the vehicles cannot be written
     */
    private void writeAll(List<Vehicle> vehicles) {
        List<String> lines = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            String line =
                    vehicle.getId()
                            + "," + vehicle.getType()
                            + "," + vehicle.getBrand()
                            + "," + vehicle.getModel()
                            + "," + vehicle.getPricePerDay()
                            + "," + vehicle.getStatus();

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
                    "Could not save vehicles file.",
                    exception
            );
        }
    }
}