package com.vrms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vrms.persistence.FileManagerRepository;
import com.vrms.persistence.ManagerRepository;

class AuthServiceTest {

    @TempDir
    Path tempDir;

    private AuthService authService;

    @BeforeEach
    void setUp() throws IOException {
        Path managersFile =
                tempDir.resolve("managers.txt");

        Files.write(
                managersFile,
                Arrays.asList("admin,1234"),
                StandardCharsets.UTF_8
        );

        ManagerRepository managerRepository =
                new FileManagerRepository(managersFile);

        authService = new AuthService(
                managerRepository
        );
    }

    @Test
    void login_correctCredentials_returnsTrue() {
        boolean result =
                authService.login("admin", "1234");

        assertTrue(result);
        assertTrue(authService.isLoggedIn());
        assertNotNull(authService.getCurrentManager());

        assertEquals(
                "admin",
                authService.getCurrentManager()
                        .getUsername()
        );
    }

    @Test
    void login_wrongPassword_returnsFalse() {
        boolean result =
                authService.login("admin", "1111");

        assertFalse(result);
        assertFalse(authService.isLoggedIn());
        assertNull(authService.getCurrentManager());
    }

    @Test
    void login_unknownUsername_returnsFalse() {
        boolean result =
                authService.login("user", "1234");

        assertFalse(result);
        assertFalse(authService.isLoggedIn());
        assertNull(authService.getCurrentManager());
    }

    @Test
    void login_emptyUsername_returnsFalse() {
        boolean result =
                authService.login("   ", "1234");

        assertFalse(result);
        assertFalse(authService.isLoggedIn());
        assertNull(authService.getCurrentManager());
    }

    @Test
    void login_emptyPassword_returnsFalse() {
        boolean result =
                authService.login("admin", "");

        assertFalse(result);
        assertFalse(authService.isLoggedIn());
        assertNull(authService.getCurrentManager());
    }

    @Test
    void login_usernameWithSpaces_returnsTrue() {
        boolean result =
                authService.login("  admin  ", "1234");

        assertTrue(result);
        assertTrue(authService.isLoggedIn());
        assertNotNull(authService.getCurrentManager());
    }

    @Test
    void logout_loggedInManager_clearsSession() {
        authService.login("admin", "1234");

        authService.logout();

        assertFalse(authService.isLoggedIn());
        assertNull(authService.getCurrentManager());
    }

    @Test
    void usernameExists_existingUsername_returnsTrue() {
        assertTrue(
                authService.usernameExists("admin")
        );
    }

    @Test
    void usernameExists_unknownUsername_returnsFalse() {
        assertFalse(
                authService.usernameExists("user")
        );
    }

    @Test
    void usernameExists_emptyUsername_returnsFalse() {
        assertFalse(
                authService.usernameExists(" ")
        );
    }

    @Test
    void getCurrentManager_beforeLogin_returnsNull() {
        assertNull(
                authService.getCurrentManager()
        );
    }

    @Test
    void constructor_nullRepository_throwsException() {
        assertThrows(
                NullPointerException.class,
                () -> new AuthService(null)
        );
    }
}