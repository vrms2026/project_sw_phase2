package com.vrms.presentation;

import java.util.Objects;

import com.vrms.application.AuthService;

/**
 * Handles manager login and logout requests from the presentation layer.
 */
public class ManagerLoginController {

    /**
     * Authentication service used to manage the manager session.
     */
    private final AuthService authService;

    /**
     * Creates a manager login controller.
     *
     * @param authService service used for authentication operations
     * @throws NullPointerException if authService is null
     */
    public ManagerLoginController(AuthService authService) {
        this.authService = Objects.requireNonNull(
                authService,
                "Authentication service cannot be null."
        );
    }

    /**
     * Attempts to log in a manager using the provided credentials.
     *
     * @param username manager username
     * @param password manager password
     * @return a message describing the login result
     */
    public String login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return "Username cannot be empty";
        }

        if (password == null || password.trim().isEmpty()) {
            return "Password cannot be empty";
        }

        if (!authService.usernameExists(username)) {
            return "Username not found";
        }

        if (!authService.login(username, password)) {
            return "Incorrect password";
        }

        return "Login successful";
    }

    /**
     * Logs out the currently authenticated manager.
     *
     * @return a message describing the logout result
     */
    public String logout() {
        if (!authService.isLoggedIn()) {
            return "No manager is logged in";
        }

        authService.logout();
        return "Logout successful";
    }

    /**
     * Checks whether a manager is currently logged in.
     *
     * @return true if a manager is logged in, otherwise false
     */
    public boolean isLoggedIn() {
        return authService.isLoggedIn();
    }
}