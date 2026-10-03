package com.framework.pages.practice;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class HoversPage extends PracticePage {

    private static final By FIGURES = By.cssSelector(".figure");
    private static final By CAPTION = By.cssSelector(".figcaption");
    private static final By CAPTION_NAME = By.cssSelector(".figcaption h5");

    public HoversPage(WebDriver driver) {
        super(driver);
    }

    public HoversPage open() {
        openPath("/hovers");
        waitVisible(FIGURES);
        return this;
    }

    public int getFigureCount() {
        return count(FIGURES);
    }

    public void hoverOverFigure(int index) {
        List<WebElement> figures = driver.findElements(FIGURES);
        hover(figures.get(index), "user image " + (index + 1));
    }

    public boolean isCaptionDisplayed(int index) {
        return driver.findElements(FIGURES).get(index).findElement(CAPTION).isDisplayed();
    }

    public String getCaptionName(int index) {
        return driver.findElements(FIGURES).get(index).findElement(CAPTION_NAME).getText().trim();
    }
}
