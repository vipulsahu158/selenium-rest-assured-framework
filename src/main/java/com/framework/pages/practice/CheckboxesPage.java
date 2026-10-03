package com.framework.pages.practice;

import com.framework.reports.ExtentTestManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CheckboxesPage extends PracticePage {

    private static final By BOXES = By.cssSelector("#checkboxes input[type='checkbox']");

    public CheckboxesPage(WebDriver driver) {
        super(driver);
    }

    public CheckboxesPage open() {
        openPath("/checkboxes");
        waitVisible(BOXES);
        return this;
    }

    public boolean isChecked(int index) {
        return driver.findElements(BOXES).get(index).isSelected();
    }

    /** Clicks the checkbox only if it is not already in the wanted state. */
    public void setChecked(int index, boolean checked) {
        WebElement box = driver.findElements(BOXES).get(index);
        if (box.isSelected() != checked) {
            ExtentTestManager.log((checked ? "Tick" : "Untick") + " checkbox " + (index + 1));
            box.click();
        }
    }
}
