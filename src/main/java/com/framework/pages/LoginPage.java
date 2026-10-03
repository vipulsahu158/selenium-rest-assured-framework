package com.framework.pages;

import com.framework.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By USERNAME = By.id("user-name");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        open(ConfigReader.get("ui.base.url"));
        waitVisible(LOGIN_BUTTON);
        return this;
    }

    public LoginPage enterUsername(String username) {
        type(USERNAME, username, "username");
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(PASSWORD, password, "password");
        return this;
    }

    public void clickLogin() {
        click(LOGIN_BUTTON, "Login button");
    }

    /** Performs the login action; the caller decides which page to expect next. */
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }
}
