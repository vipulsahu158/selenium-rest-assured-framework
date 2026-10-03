package com.framework.utils;

import com.framework.config.ConfigReader;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class ScreenshotUtils {

    private ScreenshotUtils() {
    }

    public static String asBase64(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
    }

    /** Saves a PNG under screenshot.dir and returns its path. */
    public static String saveToFile(WebDriver driver, String name) {
        try {
            Path dir = Paths.get(ConfigReader.get("screenshot.dir", "reports/screenshots"));
            Files.createDirectories(dir);
            String safeName = name.replaceAll("[^a-zA-Z0-9._-]", "_");
            String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            Path file = dir.resolve(safeName + "_" + stamp + ".png");
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(file, png);
            return file.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new IllegalStateException("Could not save screenshot", e);
        }
    }
}
