package com.framework.tests.ui.practice;

import com.framework.config.ConfigReader;
import com.framework.driver.DriverManager;
import com.framework.pages.practice.CheckboxesPage;
import com.framework.pages.practice.DropdownPage;
import com.framework.pages.practice.FileUploadPage;
import com.framework.pages.practice.KeyPressesPage;
import com.framework.pages.practice.LoginFormPage;
import com.framework.pages.practice.SliderPage;
import com.framework.tests.ui.BaseTest;
import org.openqa.selenium.Keys;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/** Typing, clicking and choosing: login form, checkboxes, dropdown, keyboard, slider, file upload. */
public class FormInteractionTests extends BaseTest {

    @DataProvider(name = "invalidLogins", parallel = true)
    public Object[][] invalidLogins() {
        return new Object[][]{
                {Map.of("TestCaseId", "PL01", "Description", "wrong username",
                        "Username", "nobody", "Password", ConfigReader.get("practice.password"),
                        "Expected", "Your username is invalid!")},
                {Map.of("TestCaseId", "PL02", "Description", "wrong password",
                        "Username", ConfigReader.get("practice.username"), "Password", "wrong",
                        "Expected", "Your password is invalid!")},
                {Map.of("TestCaseId", "PL03", "Description", "empty credentials",
                        "Username", "", "Password", "",
                        "Expected", "Your username is invalid!")},
        };
    }

    @Test(groups = {"practice", "smoke"}, description = "Valid login opens the secure area and logout returns to the form")
    public void loginAndLogout() {
        LoginFormPage page = new LoginFormPage(DriverManager.getDriver()).open();
        page.login(ConfigReader.get("practice.username"), ConfigReader.get("practice.password"));

        Assert.assertTrue(page.getFlashMessage().contains("You logged into a secure area!"), page.getFlashMessage());
        Assert.assertTrue(page.getCurrentUrl().endsWith("/secure"), "URL: " + page.getCurrentUrl());

        page.logout();
        Assert.assertTrue(page.getFlashMessage().contains("You logged out of the secure area!"), page.getFlashMessage());
        Assert.assertTrue(page.getCurrentUrl().endsWith("/login"), "URL: " + page.getCurrentUrl());
    }

    @Test(dataProvider = "invalidLogins", groups = {"practice", "regression"})
    public void invalidLoginShowsError(Map<String, String> data) {
        LoginFormPage page = new LoginFormPage(DriverManager.getDriver()).open();
        page.login(data.get("Username"), data.get("Password"));

        Assert.assertTrue(page.getFlashMessage().contains(data.get("Expected")), page.getFlashMessage());
        Assert.assertFalse(page.isLogoutVisible(), "Logout must not be offered after a failed login");
    }

    @Test(groups = {"practice", "regression"}, description = "Checkboxes can be ticked and unticked")
    public void checkboxes() {
        CheckboxesPage page = new CheckboxesPage(DriverManager.getDriver()).open();
        Assert.assertFalse(page.isChecked(0), "Checkbox 1 starts unticked");
        Assert.assertTrue(page.isChecked(1), "Checkbox 2 starts ticked");

        page.setChecked(0, true);
        page.setChecked(1, false);

        Assert.assertTrue(page.isChecked(0), "Checkbox 1 should be ticked");
        Assert.assertFalse(page.isChecked(1), "Checkbox 2 should be unticked");
    }

    @Test(groups = {"practice", "regression"}, description = "Dropdown lists its options and selects by visible text")
    public void dropdown() {
        DropdownPage page = new DropdownPage(DriverManager.getDriver()).open();
        Assert.assertEquals(page.getOptions(), List.of("Please select an option", "Option 1", "Option 2"));

        page.selectByText("Option 2");
        Assert.assertEquals(page.getSelectedText(), "Option 2");
        page.selectByText("Option 1");
        Assert.assertEquals(page.getSelectedText(), "Option 1");
    }

    @Test(groups = {"practice", "regression"}, description = "Keyboard keys are detected by the page")
    public void keyboardInput() {
        KeyPressesPage page = new KeyPressesPage(DriverManager.getDriver()).open();

        page.press("A");
        Assert.assertEquals(page.getResult(), "You entered: A");
        // not ENTER: the input sits in a <form>, so Enter submits it and reloads the page
        page.press(Keys.SPACE);
        Assert.assertEquals(page.getResult(), "You entered: SPACE");
        page.press(Keys.ARROW_LEFT);
        Assert.assertEquals(page.getResult(), "You entered: LEFT");
    }

    @Test(groups = {"practice", "regression"}, description = "Setting the slider updates the value shown next to it")
    public void slider() {
        SliderPage page = new SliderPage(DriverManager.getDriver()).open();
        Assert.assertEquals(page.getValue(), "0");

        page.setValue(3.5);

        Assert.assertEquals(page.getSliderPosition(), "3.5");
        Assert.assertEquals(page.getValue(), "3.5");
    }

    @Test(groups = {"practice", "regression"}, description = "A file can be uploaded and its name is shown")
    public void fileUpload() throws IOException {
        Path file = Files.createTempFile("upload-", ".txt");
        try {
            Files.writeString(file, "uploaded by Selenium");
            FileUploadPage page = new FileUploadPage(DriverManager.getDriver()).open();

            page.upload(file);

            Assert.assertEquals(page.getHeading(), "File Uploaded!");
            Assert.assertEquals(page.getUploadedFileName(), file.getFileName().toString());
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
