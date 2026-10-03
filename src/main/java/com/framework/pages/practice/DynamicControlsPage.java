package com.framework.pages.practice;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** A checkbox that is removed / added back, and a text box that is enabled / disabled, both via an Ajax call. */
public class DynamicControlsPage extends PracticePage {

    private static final By CHECKBOX = By.cssSelector("#checkbox-example #checkbox");
    private static final By CHECKBOX_BUTTON = By.cssSelector("#checkbox-example button");
    private static final By CHECKBOX_MESSAGE = By.cssSelector("#checkbox-example #message");
    private static final By INPUT = By.cssSelector("#input-example input");
    private static final By INPUT_BUTTON = By.cssSelector("#input-example button");
    private static final By INPUT_MESSAGE = By.cssSelector("#input-example #message");

    public DynamicControlsPage(WebDriver driver) {
        super(driver);
    }

    public DynamicControlsPage open() {
        openPath("/dynamic_controls");
        waitVisible(CHECKBOX_BUTTON);
        return this;
    }

    public void removeCheckbox() {
        click(CHECKBOX_BUTTON, "Remove button");
        wait.until(ExpectedConditions.textToBePresentInElementLocated(CHECKBOX_MESSAGE, "It's gone!"));
    }

    public void addCheckbox() {
        click(CHECKBOX_BUTTON, "Add button");
        wait.until(ExpectedConditions.textToBePresentInElementLocated(CHECKBOX_MESSAGE, "It's back!"));
    }

    public boolean isCheckboxPresent() {
        return count(CHECKBOX) > 0;
    }

    public void enableInput() {
        click(INPUT_BUTTON, "Enable button");
        wait.until(ExpectedConditions.textToBePresentInElementLocated(INPUT_MESSAGE, "It's enabled!"));
    }

    public void disableInput() {
        click(INPUT_BUTTON, "Disable button");
        wait.until(ExpectedConditions.textToBePresentInElementLocated(INPUT_MESSAGE, "It's disabled!"));
    }

    public boolean isInputEnabled() {
        return driver.findElement(INPUT).isEnabled();
    }

    public void typeInInput(String text) {
        type(INPUT, text, "the enabled text box");
    }

    public String getInputValue() {
        return driver.findElement(INPUT).getAttribute("value");
    }
}
