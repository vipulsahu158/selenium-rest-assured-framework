package com.framework.pages.practice;

import com.framework.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** HTTP Basic Auth: the credentials travel in the URL (https://user:pass@host/path), so no dialog appears. */
public class BasicAuthPage extends PracticePage {

    private static final By MESSAGE = By.cssSelector(".example p");

    public BasicAuthPage(WebDriver driver) {
        super(driver);
    }

    public BasicAuthPage openWithCredentials(String user, String password) {
        String base = ConfigReader.get("practice.base.url");
        open(base.replace("://", "://" + user + ":" + password + "@") + "/basic_auth");
        waitVisible(MESSAGE);
        return this;
    }

    public String getMessage() {
        return getText(MESSAGE);
    }
}
