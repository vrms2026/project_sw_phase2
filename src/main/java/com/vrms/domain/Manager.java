package com.vrms.domain;

/**
 * Represents a manager who can access the vehicle rental system.
 */
public class Manager {

    /**
     * The manager's username.
     */
    private final String username;

    /**
     * The manager's password.
     */
    private final String password;

    /**
     * Creates a manager with the provided credentials.
     *
     * @param username the manager's username
     * @param password the manager's password
     */
    public Manager(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Returns the manager's username.
     *
     * @return the manager's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the manager's password.
     *
     * @return the manager's password
     */
    public String getPassword() {
        return password;
    }
}