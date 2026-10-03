package com.framework.tests.ui.practice;

import com.framework.config.ConfigReader;
import com.framework.driver.DriverManager;
import com.framework.pages.practice.BasicAuthPage;
import com.framework.pages.practice.ContextMenuPage;
import com.framework.pages.practice.JavaScriptAlertsPage;
import com.framework.pages.practice.NestedFramesPage;
import com.framework.pages.practice.WindowsPage;
import com.framework.tests.ui.BaseTest;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/** Things that live outside the page body: dialogs, frames, extra windows, HTTP auth, right-click. */
public class BrowserHandlingTests extends BaseTest {

    @Test(groups = {"practice", "smoke"}, description = "JS alert is read and accepted")
    public void jsAlert() {
        JavaScriptAlertsPage page = new JavaScriptAlertsPage(DriverManager.getDriver()).open();

        Assert.assertEquals(page.acceptAlert(), "I am a JS Alert");
        Assert.assertEquals(page.getResult(), "You successfully clicked an alert");
    }

    @Test(groups = {"practice", "regression"}, description = "JS confirm can be accepted or dismissed")
    public void jsConfirm() {
        JavaScriptAlertsPage page = new JavaScriptAlertsPage(DriverManager.getDriver()).open();

        Assert.assertEquals(page.acceptConfirm(), "I am a JS Confirm");
        Assert.assertEquals(page.getResult(), "You clicked: Ok");

        page.dismissConfirm();
        Assert.assertEquals(page.getResult(), "You clicked: Cancel");
    }

    @Test(groups = {"practice", "regression"}, description = "JS prompt takes typed text or is cancelled")
    public void jsPrompt() {
        JavaScriptAlertsPage page = new JavaScriptAlertsPage(DriverManager.getDriver()).open();

        page.acceptPrompt("Hello Selenium");
        Assert.assertEquals(page.getResult(), "You entered: Hello Selenium");

        page.dismissPrompt();
        Assert.assertEquals(page.getResult(), "You entered: null");
    }

    @Test(groups = {"practice", "regression"}, description = "Text is read from frames nested inside a frameset")
    public void nestedFrames() {
        NestedFramesPage page = new NestedFramesPage(DriverManager.getDriver()).open();

        Assert.assertEquals(page.getFrameText("frame-top", "frame-left"), "LEFT");
        Assert.assertEquals(page.getFrameText("frame-top", "frame-middle"), "MIDDLE");
        Assert.assertEquals(page.getFrameText("frame-top", "frame-right"), "RIGHT");
        Assert.assertEquals(page.getFrameText("frame-bottom"), "BOTTOM");
    }

    @Test(groups = {"practice", "smoke"}, description = "A link opens a second window; switch to it and back")
    public void multipleWindows() {
        WindowsPage page = new WindowsPage(DriverManager.getDriver()).open();

        String original = page.openNewWindow();

        Assert.assertEquals(page.getWindowCount(), 2);
        Assert.assertEquals(page.getTitle(), "New Window");
        Assert.assertEquals(page.getNewWindowHeading(), "New Window");

        page.closeCurrentAndSwitchTo(original);
        Assert.assertEquals(page.getWindowCount(), 1);
        Assert.assertEquals(page.getTitle(), "The Internet");
    }

    @Test(groups = {"practice", "regression"}, description = "HTTP Basic Auth with credentials in the URL")
    public void basicAuth() {
        // Firefox asks for confirmation before using credentials from a URL, so this check is Chromium-only
        if (DriverManager.getDriver() instanceof FirefoxDriver) {
            throw new SkipException("Credentials in the URL need a confirmation prompt in Firefox");
        }
        BasicAuthPage page = new BasicAuthPage(DriverManager.getDriver());

        page.openWithCredentials("admin", "admin");

        Assert.assertTrue(page.getMessage().contains("Congratulations! You must have the proper credentials."),
                page.getMessage());
    }

    @Test(groups = {"practice", "regression"}, description = "Right-click shows a JS alert")
    public void contextMenu() {
        ContextMenuPage page = new ContextMenuPage(DriverManager.getDriver()).open();

        Assert.assertEquals(page.rightClickHotSpot(), "You selected a context menu");
        Assert.assertTrue(page.getCurrentUrl().startsWith(ConfigReader.get("practice.base.url")));
    }
}
