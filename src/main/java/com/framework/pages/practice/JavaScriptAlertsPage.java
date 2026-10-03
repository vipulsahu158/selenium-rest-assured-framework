package com.framework.pages.practice;

import com.framework.reports.ExtentTestManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** The three kinds of JavaScript dialog: alert (OK), confirm (OK / Cancel) and prompt (text input). */
public class JavaScriptAlertsPage extends PracticePage {

    private static final By ALERT_BUTTON = By.cssSelector("button[onclick='jsAlert()']");
    private static final By CONFIRM_BUTTON = By.cssSelector("button[onclick='jsConfirm()']");
    private static final By PROMPT_BUTTON = By.cssSelector("button[onclick='jsPrompt()']");
    private static final By RESULT = By.id("result");

    public JavaScriptAlertsPage(WebDriver driver) {
        super(driver);
    }

    public JavaScriptAlertsPage open() {
        openPath("/javascript_alerts");
        waitVisible(ALERT_BUTTON);
        return this;
    }

    private Alert waitForAlert() {
        return wait.until(ExpectedConditions.alertIsPresent());
    }

    /** Clicks "JS Alert", accepts it and returns the text the dialog showed. */
    public String acceptAlert() {
        click(ALERT_BUTTON, "JS Alert button");
        Alert alert = waitForAlert();
        String text = alert.getText();
        ExtentTestManager.log("Accept alert: " + text);
        alert.accept();
        return text;
    }

    public String acceptConfirm() {
        click(CONFIRM_BUTTON, "JS Confirm button");
        Alert alert = waitForAlert();
        String text = alert.getText();
        ExtentTestManager.log("Accept confirm: " + text);
        alert.accept();
        return text;
    }

    public String dismissConfirm() {
        click(CONFIRM_BUTTON, "JS Confirm button");
        Alert alert = waitForAlert();
        String text = alert.getText();
        ExtentTestManager.log("Dismiss confirm: " + text);
        alert.dismiss();
        return text;
    }

    public void acceptPrompt(String input) {
        click(PROMPT_BUTTON, "JS Prompt button");
        Alert alert = waitForAlert();
        ExtentTestManager.log("Type '" + input + "' in the prompt and accept");
        alert.sendKeys(input);
        alert.accept();
    }

    public void dismissPrompt() {
        click(PROMPT_BUTTON, "JS Prompt button");
        ExtentTestManager.log("Dismiss prompt");
        waitForAlert().dismiss();
    }

    public String getResult() {
        return getText(RESULT);
    }
}
