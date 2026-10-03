package com.framework.pages.practice;

import com.framework.reports.ExtentTestManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

public class DragAndDropPage extends PracticePage {

    private static final By COLUMN_A = By.id("column-a");
    private static final By COLUMN_B = By.id("column-b");
    private static final By HEADER_A = By.cssSelector("#column-a header");
    private static final By HEADER_B = By.cssSelector("#column-b header");

    public DragAndDropPage(WebDriver driver) {
        super(driver);
    }

    public DragAndDropPage open() {
        openPath("/drag_and_drop");
        waitVisible(COLUMN_A);
        return this;
    }

    public void dragAOntoB() {
        ExtentTestManager.log("Drag box A onto box B");
        new Actions(driver).dragAndDrop(waitVisible(COLUMN_A), waitVisible(COLUMN_B)).perform();
    }

    public String getBoxATitle() {
        return getText(HEADER_A);
    }

    public String getBoxBTitle() {
        return getText(HEADER_B);
    }
}
