package com.framework.pages;

import com.framework.config.ConfigReader;
import com.framework.reports.ExtentTestManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Parent of all page objects: owns the driver and wraps common actions with explicit waits
 * and report logging, so page classes stay short and tests never touch Selenium directly.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait", 15)));
    }

    protected void open(String url) {
        ExtentTestManager.log("Open " + url);
        driver.get(url);
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator, String description) {
        ExtentTestManager.log("Click " + description);
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String text, String description) {
        ExtentTestManager.log("Enter '" + text + "' in " + description);
        WebElement element = waitVisible(locator);
        element.clear();
        if (text != null && !text.isEmpty()) {
            element.sendKeys(text);
        }
    }

    protected void hover(WebElement element, String description) {
        ExtentTestManager.log("Hover over " + description);
        new Actions(driver).moveToElement(element).perform();
    }

    protected void rightClick(By locator, String description) {
        ExtentTestManager.log("Right-click " + description);
        new Actions(driver).contextClick(waitVisible(locator)).perform();
    }

    protected Object js(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    protected void scrollToBottom() {
        ExtentTestManager.log("Scroll to the bottom of the page");
        js("window.scrollTo(0, document.body.scrollHeight)");
    }

    protected String getText(By locator) {
        return waitVisible(locator).getText().trim();
    }

    protected boolean isDisplayed(By locator) {
        List<WebElement> found = driver.findElements(locator);
        return !found.isEmpty() && found.get(0).isDisplayed();
    }

    protected int count(By locator) {
        return driver.findElements(locator).size();
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
