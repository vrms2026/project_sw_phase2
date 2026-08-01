package com.vrms.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vrms.domain.Rental;
import com.vrms.domain.RentalStatus;
import com.vrms.domain.Vehicle;

public class FileRentalRepositoryTest {

    @TempDir
    Path tempDir;

    private FileVehicleRepository vehicleRepository;
    private FileRentalRepository rentalRepository;

    @BeforeEach
    public void setUp() {
        vehicleRepository =
                new FileVehicleRepository(
                        tempDir.resolve("vehicles.txt")
                );

        rentalRepository =
                new FileRentalRepository(
                        tempDir.resolve("rentals.txt"),
                        vehicleRepository
                );
    }

    @Test
    public void saveAndRead_shouldPersistTotalCost() {
        Vehicle vehicle =
                vehicleRepository.findById("V1");

        Rental rental = new Rental(
                "R1",
                vehicle,
                "Ahmad",
                "ahmad@example.com",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                RentalStatus.CLOSED
        );

        rental.setTotalCost(180.0);

        rentalRepository.save(rental);

        Rental savedRental =
                rentalRepository.findById("R1");

        assertEquals(
                RentalStatus.CLOSED,
                savedRental.getStatus()
        );

        assertEquals(
                180.0,
                savedRental.getTotalCost(),
                0.001
        );
    }

    @Test
    public void findAll_oldSevenColumnRecord_shouldUseZeroCost()
            throws Exception {

        Path rentalsFile =
                tempDir.resolve("rentals.txt");

        Files.write(
                rentalsFile,
                Arrays.asList(
                        "R2,Sara,sara@example.com,"
                                + "V1,2026-07-01,"
                                + "2026-07-05,ACTIVE"
                ),
                StandardCharsets.UTF_8
        );

        Rental savedRental =
                rentalRepository.findById("R2");

        assertEquals(
                0.0,
                savedRental.getTotalCost(),
                0.001
        );
    }

    @Test
    public void update_shouldReplaceRentalInsteadOfCreatingDuplicate() {
        Vehicle vehicle =
                vehicleRepository.findById("V1");

        Rental rental = new Rental(
                "R3",
                vehicle,
                "Omar",
                "omar@example.com",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                RentalStatus.ACTIVE
        );

        rentalRepository.save(rental);

        rental.closeRental();
        rental.setTotalCost(160.0);

        rentalRepository.update(rental);

        assertEquals(
                1,
                rentalRepository.findAll().size()
        );

        assertEquals(
                RentalStatus.CLOSED,
                rentalRepository.findById("R3").getStatus()
        );

        assertEquals(
                160.0,
                rentalRepository.findById("R3").getTotalCost(),
                0.001
        );
    }
    @Test
    public void constructor_whenArgumentsAreNull_shouldThrowException() {
        IllegalArgumentException nullPathException = assertThrows(IllegalArgumentException.class, () -> new FileRentalRepository((Path) null, vehicleRepository));
        IllegalArgumentException nullRepositoryException = assertThrows(IllegalArgumentException.class, () -> new FileRentalRepository(tempDir.resolve("other-rentals.txt"), null));

        assertEquals("File path cannot be null.", nullPathException.getMessage());
        assertEquals("Vehicle repository cannot be null.", nullRepositoryException.getMessage());
    }

    @Test
    public void save_whenRentalIsNull_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> rentalRepository.save(null));

        assertEquals("Rental cannot be null.", exception.getMessage());
    }

    @Test
    public void save_whenRentalIdIsNullOrBlank_shouldThrowException() {
        Vehicle vehicle = vehicleRepository.findById("V1");
        Rental nullIdRental = new Rental(null, vehicle, "Ahmad", "ahmad@example.com", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5), RentalStatus.ACTIVE);
        Rental blankIdRental = new Rental("   ", vehicle, "Ahmad", "ahmad@example.com", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5), RentalStatus.ACTIVE);

        IllegalArgumentException nullIdException = assertThrows(IllegalArgumentException.class, () -> rentalRepository.save(nullIdRental));
        IllegalArgumentException blankIdException = assertThrows(IllegalArgumentException.class, () -> rentalRepository.save(blankIdRental));

        assertEquals("Rental ID cannot be empty.", nullIdException.getMessage());
        assertEquals("Rental ID cannot be empty.", blankIdException.getMessage());
    }

    @Test
    public void findById_whenIdIsNullBlankOrMissing_shouldReturnNull() {
        assertNull(rentalRepository.findById(null));
        assertNull(rentalRepository.findById(""));
        assertNull(rentalRepository.findById("   "));
        assertNull(rentalRepository.findById("UNKNOWN"));
    }

    @Test
    public void findAll_shouldIgnoreBlankMalformedAndUnknownVehicleRecords() throws Exception {
        Path rentalsFile = tempDir.resolve("rentals.txt");

        Files.write(rentalsFile, Arrays.asList("", "invalid-record", "R4,Sara,sara@example.com,UNKNOWN,2026-07-01,2026-07-05,ACTIVE,100.0", "R5,Lina,lina@example.com,V1,2026-07-01,2026-07-05,ACTIVE,"), StandardCharsets.UTF_8);

        List<Rental> rentals = rentalRepository.findAll();

        assertEquals(1, rentals.size());
        assertEquals("R5", rentals.get(0).getRentalId());
        assertEquals(0.0, rentals.get(0).getTotalCost(), 0.001);
    }

    @Test
    public void findAll_whenFileCannotBeRead_shouldThrowException() throws Exception {
        Path rentalsFile = tempDir.resolve("rentals.txt");
        Files.delete(rentalsFile);
        Files.createDirectory(rentalsFile);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> rentalRepository.findAll());

        assertEquals("Could not read rentals file.", exception.getMessage());
    }
}