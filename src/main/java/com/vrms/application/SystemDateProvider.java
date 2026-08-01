package com.vrms.application;

import java.time.LocalDate;

/**
 * Provides the real current date from the system clock.
 */
public class SystemDateProvider implements DateProvider {

    /**
     * Returns the current system date.
     *
     * @return current system date
     */
    @Override
    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }
}