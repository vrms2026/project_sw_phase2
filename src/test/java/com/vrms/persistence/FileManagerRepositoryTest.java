package com.vrms.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vrms.domain.Manager;

class FileManagerRepositoryTest {

    @TempDir
    Path tempDir;

    private Path managersFile;
    private FileManagerRepository repository;

    @BeforeEach
    void setUp() {
        managersFile = tempDir.resolve("managers.txt");
        repository = new FileManagerRepository(managersFile);
    }

    @Test
    void constructor_shouldCreateFileAndDefaultAdmin() {
        assertTrue(Files.exists(managersFile));

        Manager manager = repository.findByUsername("admin");

        assertNotNull(manager);
        assertEquals("admin", manager.getUsername());
        assertEquals("1234", manager.getPassword());
    }

    @Test
    void constructor_whenFileAlreadyContainsData_shouldNotOverwriteIt() throws Exception {
        Path existingFile = tempDir.resolve("existing-managers.txt");
        Files.writeString(existingFile, "leen,secret", StandardCharsets.UTF_8);

        FileManagerRepository existingRepository = new FileManagerRepository(existingFile);
        Manager manager = existingRepository.findByUsername("leen");

        assertNotNull(manager);
        assertEquals("secret", manager.getPassword());
    }

    @Test
    void constructor_whenPathIsNull_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new FileManagerRepository(null));

        assertEquals("File path cannot be null.", exception.getMessage());
    }

    @Test
    void constructor_whenFileCannotBeCreated_shouldThrowException() throws Exception {
        Path blocker = tempDir.resolve("blocker");
        Files.writeString(blocker, "not a directory", StandardCharsets.UTF_8);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> new FileManagerRepository(blocker.resolve("managers.txt")));

        assertEquals("Could not create managers file.", exception.getMessage());
    }

    @Test
    void save_shouldPersistManagerAndTrimUsername() {
        repository.save(new Manager("  leen  ", "pass123"));

        Manager savedManager = repository.findByUsername(" leen ");

        assertNotNull(savedManager);
        assertEquals("leen", savedManager.getUsername());
        assertEquals("pass123", savedManager.getPassword());
    }

    @Test
    void save_whenManagerIsNull_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> repository.save(null));

        assertEquals("Manager cannot be null.", exception.getMessage());
    }

    @Test
    void save_whenUsernameIsInvalid_shouldThrowException() {
        IllegalArgumentException nullUsernameException = assertThrows(IllegalArgumentException.class, () -> repository.save(new Manager(null, "password")));
        IllegalArgumentException blankUsernameException = assertThrows(IllegalArgumentException.class, () -> repository.save(new Manager("   ", "password")));

        assertEquals("Username cannot be empty.", nullUsernameException.getMessage());
        assertEquals("Username cannot be empty.", blankUsernameException.getMessage());
    }

    @Test
    void save_whenPasswordIsInvalid_shouldThrowException() {
        IllegalArgumentException nullPasswordException = assertThrows(IllegalArgumentException.class, () -> repository.save(new Manager("manager1", null)));
        IllegalArgumentException emptyPasswordException = assertThrows(IllegalArgumentException.class, () -> repository.save(new Manager("manager2", "")));

        assertEquals("Password cannot be empty.", nullPasswordException.getMessage());
        assertEquals("Password cannot be empty.", emptyPasswordException.getMessage());
    }

    @Test
    void save_whenUsernameAlreadyExists_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> repository.save(new Manager("admin", "newPassword")));

        assertEquals("Username already exists.", exception.getMessage());
    }

    @Test
    void findByUsername_whenUsernameIsInvalid_shouldReturnNull() {
        assertNull(repository.findByUsername(null));
        assertNull(repository.findByUsername(""));
        assertNull(repository.findByUsername("   "));
    }

    @Test
    void findByUsername_shouldSkipBlankAndMalformedRecords() throws Exception {
        Files.write(managersFile, Arrays.asList("", "invalid", "other,password", "target,secret,extra", "target,secret"), StandardCharsets.UTF_8);

        Manager manager = repository.findByUsername(" target ");

        assertNotNull(manager);
        assertEquals("target", manager.getUsername());
        assertEquals("secret", manager.getPassword());
        assertNull(repository.findByUsername("missing"));
    }

    @Test
    void findByUsername_whenFileCannotBeRead_shouldThrowException() throws Exception {
        Files.delete(managersFile);
        Files.createDirectory(managersFile);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> repository.findByUsername("admin"));

        assertEquals("Could not read managers file.", exception.getMessage());
    }
}