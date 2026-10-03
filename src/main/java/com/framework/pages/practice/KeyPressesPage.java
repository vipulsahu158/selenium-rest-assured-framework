package com.framework.pages.practice;

import com.framework.reports.ExtentTestManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class KeyPressesPage extends PracticePage {

    private static final By INPUT = By.id("target");
    private static final By RESULT = By.id("result");

    public KeyPressesPage(WebDriver driver) {
        super(driver);
    }

    public KeyPressesPage open() {
        openPath("/key_presses");
        waitVisible(INPUT);
        return this;
    }

    public void press(CharSequence key) {
        ExtentTestManager.log("Press key " + key);
        waitVisible(INPUT).sendKeys(key);
    }

    public String getResult() {
        return getText(RESULT);
    }
}
