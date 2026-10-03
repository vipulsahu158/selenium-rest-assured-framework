package com.framework.driver;

import com.framework.config.ConfigReader;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates browser sessions. Local drivers are resolved automatically by Selenium Manager
 * (no WebDriverManager / manual driver downloads needed). If grid.url is set, sessions are
 * created on the remote grid instead.
 */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver createDriver(String browser) {
        String name = (browser == null || browser.isBlank()) ? ConfigReader.get("browser", "chrome") : browser;
        boolean headless = ConfigReader.getBoolean("headless");
        String grid = ConfigReader.get("grid.url");

        WebDriver driver;
        switch (name.toLowerCase()) {
            case "chrome": {
                ChromeOptions options = new ChromeOptions();
                applyLoadStrategy(options);
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--window-size=1920,1080", "--disable-notifications",
                        "--disable-search-engine-choice-screen", "--remote-allow-origins=*");
                Map<String, Object> prefs = new HashMap<>();
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                prefs.put("profile.password_manager_leak_detection", false);
                options.setExperimentalOption("prefs", prefs);
                driver = grid.isEmpty() ? new ChromeDriver(options) : remote(grid, options);
                break;
            }
            case "firefox": {
                FirefoxOptions options = new FirefoxOptions();
                applyLoadStrategy(options);
                if (headless) {
                    options.addArguments("-headless");
                }
                options.addArguments("--width=1920", "--height=1080");
                driver = grid.isEmpty() ? new FirefoxDriver(options) : remote(grid, options);
                break;
            }
            case "edge": {
                EdgeOptions options = new EdgeOptions();
                applyLoadStrategy(options);
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--window-size=1920,1080", "--disable-notifications");
                driver = grid.isEmpty() ? new EdgeDriver(options) : remote(grid, options);
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported browser: " + name
                        + " (supported: chrome, firefox, edge)");
        }

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getInt("page.load.timeout", 60)));
        if (!headless && grid.isEmpty()) {
            driver.manage().window().maximize();
        }
        return driver;
    }

    /**
     * "eager" returns once the DOM is ready instead of waiting for every image, font and ad on the page.
     * Slow third-party resources otherwise stall driver.get(); all page objects use explicit waits anyway.
     */
    private static void applyLoadStrategy(AbstractDriverOptions<?> options) {
        String strategy = ConfigReader.get("page.load.strategy", "eager").toLowerCase();
        options.setPageLoadStrategy(PageLoadStrategy.valueOf(strategy.toUpperCase()));
    }

    private static WebDriver remote(String gridUrl, org.openqa.selenium.Capabilities capabilities) {
        try {
            return new RemoteWebDriver(new URL(gridUrl), capabilities);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid grid.url: " + gridUrl, e);
        }
    }
}
