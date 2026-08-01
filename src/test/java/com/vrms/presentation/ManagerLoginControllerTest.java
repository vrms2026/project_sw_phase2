package com.vrms.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import com.vrms.application.AuthService;
import com.vrms.persistence.FileManagerRepository;

public class ManagerLoginControllerTest {

    @TempDir
    Path tempDir;

    private ManagerLoginController controller;

    @BeforeEach
    public void setUp() throws IOException {
        Path managersFile = tempDir.resolve("managers.txt");

        Files.write(
                managersFile,
                Arrays.asList("admin,1234"),
                StandardCharsets.UTF_8
        );

        AuthService authService = new AuthService(
                new FileManagerRepository(managersFile)
        );

        controller = new ManagerLoginController(authService);
    }

    @Test
    public void loginSuccessfully() {
        String result = controller.login("admin", "1234");

        assertEquals("Login successful", result);
        assertTrue(controller.isLoggedIn());
    }

    @Test
    public void loginFailsWhenPasswordIsIncorrect() {
        String result = controller.login("admin", "1111");

        assertEquals("Incorrect password", result);
        assertFalse(controller.isLoggedIn());
    }

    @Test
    public void loginFailsWhenUsernameIsNull() {
        String result = controller.login(null, "1234");

        assertEquals("Username cannot be empty", result);
        assertFalse(controller.isLoggedIn());
    }

    @Test
    public void loginFailsWhenUsernameIsEmpty() {
        String result = controller.login("   ", "1234");

        assertEquals("Username cannot be empty", result);
        assertFalse(controller.isLoggedIn());
    }

    @Test
    public void loginFailsWhenPasswordIsNull() {
        String result = controller.login("admin", null);

        assertEquals("Password cannot be empty", result);
        assertFalse(controller.isLoggedIn());
    }

    @Test
    public void loginFailsWhenPasswordIsEmpty() {
        String result = controller.login("admin", "   ");

        assertEquals("Password cannot be empty", result);
        assertFalse(controller.isLoggedIn());
    }

    @Test
    public void loginFailsWhenUsernameDoesNotExist() {
        String result = controller.login("wrongUser", "1234");

        assertEquals("Username not found", result);
        assertFalse(controller.isLoggedIn());
    }

    @Test
    public void logoutSuccessfully() {
        controller.login("admin", "1234");

        String result = controller.logout();

        assertEquals("Logout successful", result);
        assertFalse(controller.isLoggedIn());
    }

    @Test
    public void logoutWithoutLogin() {
        String result = controller.logout();

        assertEquals("No manager is logged in", result);
        assertFalse(controller.isLoggedIn());
    }

    @Test
    public void isLoggedInReturnsFalseBeforeLogin() {
        assertFalse(controller.isLoggedIn());
    }

    @Test
    public void isLoggedInReturnsTrueAfterLogin() {
        controller.login("admin", "1234");

        assertTrue(controller.isLoggedIn());
    }

    @Test
    public void constructorFailsWhenAuthServiceIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> new ManagerLoginController(null)
        );
    }
}