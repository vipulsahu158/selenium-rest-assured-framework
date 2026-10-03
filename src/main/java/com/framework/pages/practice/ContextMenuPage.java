package com.framework.pages.practice;

import com.framework.reports.ExtentTestManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ContextMenuPage extends PracticePage {

    private static final By HOT_SPOT = By.id("hot-spot");

    public ContextMenuPage(WebDriver driver) {
        super(driver);
    }

    public ContextMenuPage open() {
        openPath("/context_menu");
        waitVisible(HOT_SPOT);
        return this;
    }

    /** Right-clicks the box; the page answers with a JavaScript alert, which is accepted here. */
    public String rightClickHotSpot() {
        rightClick(HOT_SPOT, "the hot spot box");
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        ExtentTestManager.log("Accept alert: " + text);
        alert.accept();
        return text;
    }
}
