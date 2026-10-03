package com.framework.pages.practice;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends PracticePage {

    private static final By HEADING = By.cssSelector("h1.heading");
    private static final By EXAMPLE_LINKS = By.cssSelector("#content ul li a");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage open() {
        openPath("/");
        waitVisible(HEADING);
        return this;
    }

    public String getHeading() {
        return getText(HEADING);
    }

    public int getExampleCount() {
        return count(EXAMPLE_LINKS);
    }

    public void openExample(String linkText) {
        click(By.linkText(linkText), "'" + linkText + "' link");
    }
}
