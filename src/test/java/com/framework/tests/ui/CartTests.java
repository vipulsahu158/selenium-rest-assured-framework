package com.framework.tests.ui;

import com.framework.dataproviders.DataProviders;
import com.framework.driver.DriverManager;
import com.framework.pages.InventoryPage;
import com.framework.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class CartTests extends BaseTest {

    @Test(dataProvider = "cartData", dataProviderClass = DataProviders.class, groups = {"cart", "regression"})
    public void verifyCartBadgeCount(Map<String, String> data) {
        new LoginPage(DriverManager.getDriver()).open().login(data.get("Username"), data.get("Password"));

        InventoryPage inventory = new InventoryPage(DriverManager.getDriver());
        Assert.assertTrue(inventory.isLoaded(), "Inventory page did not load");

        inventory.addProductsToCart(Integer.parseInt(data.get("ItemsToAdd")));
        Assert.assertEquals(inventory.getCartCount(), Integer.parseInt(data.get("ExpectedCartCount")),
                "Cart badge count mismatch");
    }
}
