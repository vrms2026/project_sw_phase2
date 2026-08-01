package com.vrms.application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
class SystemDateProviderTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
    void getCurrentDate_shouldReturnCurrentSystemDate() {
        LocalDate beforeCall = LocalDate.now();
        LocalDate actualDate = new SystemDateProvider().getCurrentDate();
        LocalDate afterCall = LocalDate.now();

        assertFalse(actualDate.isBefore(beforeCall));
        assertFalse(actualDate.isAfter(afterCall));
    }

}
