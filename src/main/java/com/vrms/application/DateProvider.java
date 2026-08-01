package com.vrms.application;

import java.time.LocalDate;

/**
 * Provides the current date to application services.
 *
 * <p>This abstraction allows date-dependent behavior
 * to be tested using Mockito without depending on the
 * real system date.</p>
 */
public interface DateProvider {

    /**
     * Returns the current date.
     *
     * @return current date
     */
    LocalDate getCurrentDate();
}