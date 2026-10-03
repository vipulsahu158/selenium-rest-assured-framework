package com.framework.tests.ui;

import com.framework.dataproviders.DataProviders;
import com.framework.driver.DriverManager;
import com.framework.pages.InventoryPage;
import com.framework.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class LoginTests extends BaseTest {

    @Test(dataProvider = "loginData", dataProviderClass = DataProviders.class, groups = {"login", "regression"})
    public void verifyLogin(Map<String, String> data) {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver()).open();
        loginPage.login(data.get("Username"), data.get("Password"));

        if ("SUCCESS".equalsIgnoreCase(data.get("ExpectedResult"))) {
            InventoryPage inventory = new InventoryPage(DriverManager.getDriver());
            Assert.assertTrue(inventory.isLoaded(), "Inventory page should load after a valid login");
            Assert.assertEquals(inventory.getPageTitle(), data.get("ExpectedMessage"));
        } else {
            Assert.assertTrue(loginPage.getErrorMessage().contains(data.get("ExpectedMessage")),
                    "Unexpected error message: " + loginPage.getErrorMessage());
        }
    }
}
