package com.framework.pages.practice;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** A frameset: frame-top holds frame-left / frame-middle / frame-right, frame-bottom sits below it. */
public class NestedFramesPage extends PracticePage {

    public NestedFramesPage(WebDriver driver) {
        super(driver);
    }

    public NestedFramesPage open() {
        openPath("/nested_frames");
        wait.until(d -> d.findElements(By.name("frame-bottom")).size() == 1);
        return this;
    }

    /** Switches through the given frame names (outermost first), reads the body text, then switches back. */
    public String getFrameText(String... framePath) {
        driver.switchTo().defaultContent();
        try {
            for (String frame : framePath) {
                wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frame));
            }
            // a frame can be switched to before its content has loaded, so wait for some text
            return wait.until(d -> {
                String text = d.findElement(By.tagName("body")).getText().trim();
                return text.isEmpty() ? null : text;
            });
        } finally {
            driver.switchTo().defaultContent();
        }
    }
}
