package com.framework.pages.practice;

import com.framework.config.ConfigReader;
import com.framework.pages.BasePage;
import org.openqa.selenium.WebDriver;

/** Parent of the page objects for the practice site (practice.base.url). */
public abstract class PracticePage extends BasePage {

    protected PracticePage(WebDriver driver) {
        super(driver);
    }

    protected void openPath(String path) {
        open(ConfigReader.get("practice.base.url") + path);
    }
}
