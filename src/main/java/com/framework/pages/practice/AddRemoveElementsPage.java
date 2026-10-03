package com.framework.pages.practice;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AddRemoveElementsPage extends PracticePage {

    private static final By ADD_BUTTON = By.cssSelector("button[onclick='addElement()']");
    private static final By DELETE_BUTTONS = By.cssSelector("#elements .added-manually");

    public AddRemoveElementsPage(WebDriver driver) {
        super(driver);
    }

    public AddRemoveElementsPage open() {
        openPath("/add_remove_elements/");
        waitVisible(ADD_BUTTON);
        return this;
    }

    public void addElement() {
        int before = count(DELETE_BUTTONS);
        click(ADD_BUTTON, "Add Element button");
        wait.until(d -> count(DELETE_BUTTONS) == before + 1);
    }

    public void deleteFirstElement() {
        int before = count(DELETE_BUTTONS);
        click(DELETE_BUTTONS, "the first Delete button");
        wait.until(d -> count(DELETE_BUTTONS) == before - 1);
    }

    public int getDeleteButtonCount() {
        return count(DELETE_BUTTONS);
    }
}
