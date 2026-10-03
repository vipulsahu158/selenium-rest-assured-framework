package com.framework.pages.practice;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Example 1: the result element exists but is hidden until loading ends.
 * Example 2: the result element is not in the DOM at all until loading ends.
 * Both need an explicit wait, never a fixed sleep.
 */
public class DynamicLoadingPage extends PracticePage {

    private static final By START_BUTTON = By.cssSelector("#start button");
    private static final By LOADING = By.id("loading");
    private static final By FINISH_TEXT = By.cssSelector("#finish h4");

    public DynamicLoadingPage(WebDriver driver) {
        super(driver);
    }

    public DynamicLoadingPage open(int example) {
        openPath("/dynamic_loading/" + example);
        waitVisible(START_BUTTON);
        return this;
    }

    public void start() {
        click(START_BUTTON, "Start button");
    }

    public boolean isLoadingShown() {
        return isDisplayed(LOADING);
    }

    public boolean isResultInDom() {
        return count(FINISH_TEXT) > 0;
    }

    /** Waits (up to explicit.wait seconds) for the loader to finish and returns the revealed text. */
    public String getResultText() {
        return getText(FINISH_TEXT);
    }
}
