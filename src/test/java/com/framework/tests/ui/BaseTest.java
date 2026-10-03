package com.framework.tests.ui;

import com.framework.driver.DriverFactory;
import com.framework.driver.DriverManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Starts a fresh, thread-confined browser before every test method and closes it afterwards.
 * The browser comes from the "browser" parameter of the <test> block in testng.xml.
 */
public class BaseTest {

    @BeforeMethod(alwaysRun = true)
    @Parameters("browser")
    public void setUp(@Optional("") String browser) {
        DriverManager.setDriver(DriverFactory.createDriver(browser));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverManager.quitDriver();
    }
}
