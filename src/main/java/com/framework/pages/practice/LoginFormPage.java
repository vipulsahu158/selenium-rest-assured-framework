package com.framework.pages.practice;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginFormPage extends PracticePage {

    private static final By USERNAME = By.id("username");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.cssSelector("button[type='submit']");
    private static final By FLASH = By.id("flash");
    private static final By LOGOUT = By.cssSelector("a[href='/logout']");

    public LoginFormPage(WebDriver driver) {
        super(driver);
    }

    public LoginFormPage open() {
        openPath("/login");
        waitVisible(LOGIN_BUTTON);
        return this;
    }

    public void login(String username, String password) {
        type(USERNAME, username, "username");
        type(PASSWORD, password, "password");
        click(LOGIN_BUTTON, "Login button");
    }

    public void logout() {
        click(LOGOUT, "Logout button");
    }

    public String getFlashMessage() {
        return getText(FLASH);
    }

    public boolean isLogoutVisible() {
        return isDisplayed(LOGOUT);
    }
}
