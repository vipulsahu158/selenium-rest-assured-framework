package com.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class InventoryPage extends BasePage {

    private static final By PAGE_TITLE = By.cssSelector(".title");
    private static final By PRODUCTS = By.cssSelector(".inventory_item");
    private static final By ADD_TO_CART_BUTTONS = By.cssSelector("button[id^='add-to-cart']");
    private static final By CART_BADGE = By.cssSelector(".shopping_cart_badge");
    private static final By MENU_BUTTON = By.id("react-burger-menu-btn");
    private static final By LOGOUT_LINK = By.id("logout_sidebar_link");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        try {
            return waitVisible(PAGE_TITLE).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public String getPageTitle() {
        return getText(PAGE_TITLE);
    }

    public int getProductCount() {
        return count(PRODUCTS);
    }

    /** Adds the first N not-yet-added products. The locator is re-evaluated each time as the DOM changes. */
    public InventoryPage addProductsToCart(int howMany) {
        for (int i = 0; i < howMany; i++) {
            click(ADD_TO_CART_BUTTONS, "Add to cart (product " + (i + 1) + ")");
        }
        return this;
    }

    public int getCartCount() {
        return isDisplayed(CART_BADGE) ? Integer.parseInt(getText(CART_BADGE)) : 0;
    }

    public void logout() {
        click(MENU_BUTTON, "Menu");
        click(LOGOUT_LINK, "Logout");
    }
}
