package com.framework.pages.practice;

import com.framework.reports.ExtentTestManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class WindowsPage extends PracticePage {

    private static final By CLICK_HERE = By.linkText("Click Here");
    private static final By NEW_WINDOW_HEADING = By.tagName("h3");

    public WindowsPage(WebDriver driver) {
        super(driver);
    }

    public WindowsPage open() {
        openPath("/windows");
        waitVisible(CLICK_HERE);
        return this;
    }

    /** Clicks the link that opens a second window and switches to it. Returns the original window's handle. */
    public String openNewWindow() {
        String original = driver.getWindowHandle();
        click(CLICK_HERE, "'Click Here' link");
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(original)) {
                ExtentTestManager.log("Switch to the new window");
                driver.switchTo().window(handle);
            }
        }
        return original;
    }

    public String getNewWindowHeading() {
        return getText(NEW_WINDOW_HEADING);
    }

    public void closeCurrentAndSwitchTo(String handle) {
        ExtentTestManager.log("Close this window and go back to the first one");
        driver.close();
        driver.switchTo().window(handle);
    }

    public int getWindowCount() {
        return driver.getWindowHandles().size();
    }
}
